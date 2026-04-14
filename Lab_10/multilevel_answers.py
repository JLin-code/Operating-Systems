#!/usr/bin/env python
"""Analyze multilevel page table memory references"""

import subprocess

print("="*80)
print("QUESTION 1: Registers Needed")
print("="*80)
print("""
Linear page table:    1 register (base of page table)
Two-level table:      1 register (PDBR - page directory base register)
Three-level table:    1 register (still just PDBR)

Key insight: Only the TOP LEVEL needs a register. The CPU loads intermediate
page tables from memory during the walk-down process.
""")

print("\n" + "="*80)
print("QUESTION 2: Memory References for Each Address Translation")
print("="*80)

for seed in [0, 1, 2]:
    print(f"\n>>> SEED {seed}:")
    cmd = f"python paging-multilevel-translate.py -s {seed} -c -n 5"
    result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    
    lines = result.stdout.split('\n')
    in_va_section = False
    translation_count = 0
    
    for i, line in enumerate(lines):
        if 'Virtual Address' in line and '0x' in line:
            in_va_section = True
            print(f"\n  {line.strip()}")
            
            # Collect the translation details
            mem_refs = 0
            for j in range(i+1, min(i+5, len(lines))):
                next_line = lines[j].strip()
                
                if '--> pde' in next_line:
                    print(f"    Ref 1: {next_line[:70]}")
                    mem_refs += 1
                    
                elif '--> pte' in next_line:
                    print(f"    Ref 2: {next_line[:70]}")
                    mem_refs += 1
                    
                elif '--> Translates to' in next_line:
                    print(f"    Ref 3: {next_line[:70]}")
                    mem_refs += 1
                    
                elif '--> Fault' in next_line:
                    print(f"    Result: FAULT (only used {mem_refs} ref)")
                    break
                    
                elif next_line.startswith('Virtual Address'):
                    break
                    
            if 'Fault' not in lines[i:min(i+5, len(lines))]:
                print(f"    Total memory references: {mem_refs} (PDE + PTE + data fetch)")

print("\n" + "="*80)
print("SUMMARY: Memory References Needed")
print("="*80)
print("""
For a VALID address translation in 2-level page table:
  1. Load PDE from page directory
  2. Load PTE from page table (using PDE)
  3. Load actual data from memory (using PTE)
  Total: 3 memory references

For a FAULT (invalid PTE):
  1. Load PDE from page directory
  2. Load PTE from page table (using PDE)
  3. Determine fault - no data fetch needed
  Total: 2 memory references

For a 3-level page table:
  Total would be 4 references (3 page table walks + data)
""")

print("\n" + "="*80)
print("QUESTION 3: Cache Behavior Analysis")
print("="*80)
print("""
MEMORY REFERENCES PATTERN:

In multilevel page tables, memory accesses follow a specific pattern:
1. First access: Page Directory base (from PDBR)
2. Second access: Page Table (from directory entry)
3. Third access: Data (from page table entry)

CACHE HIT PROBABILITY:

GOOD cache locality for:
- Page directories: Usually small, fit in L1/L2 cache
- Active page tables: Temporal locality (processes reuse same pages)
- Kernel data structures: Stay in cache between contexts

POOR cache locality for:
- Large working sets: Different pages scattered across memory
- Context switches: Different processes need different page tables
- Random memory access patterns: No spatial locality

EXPECTED BEHAVIOR:
→ Page directory: HIGH hit rate (often in cache)
→ Page tables: MEDIUM hit rate (depends on working set size)  
→ Data memory: VARIES (depends on application behavior)

REAL SYSTEMS FIX THIS WITH:
- TLB (Translation Lookaside Buffer): Caches address translations
  Typical TLB: 64-512 entries, hit rate 95-99%
- Multi-level caching: L1/L2/L3 caches for translations
- Software prefetching: Load likely page tables early

CONCLUSION: Without TLB, multilevel page tables cause cache
misses for every address translation. TLB caching is essential!
""")
