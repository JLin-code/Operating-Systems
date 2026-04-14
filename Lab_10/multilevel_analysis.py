#!/usr/bin/env python
"""Run multilevel page table translations and collect results"""

import subprocess
import re

print("="*80)
print("QUESTION 1: Registers Needed for Page Table Levels")
print("="*80)
print("""
Linear (1-level):   1 register (holds pointer to page table base)
Two-level:          1 register (still just need PDBR - page directory base register)
Three-level:        1 register (still just PDBR!)

All multilevel schemes need only 1 register pointing to the root table.
The CPU then walks down the levels to find the final entry.
""")

print("="*80)
print("QUESTION 2: Memory References for Each Lookup")
print("="*80)

def extract_mem_refs(output):
    """Count memory references from translation output"""
    lines = output.split('\n')
    mem_refs = []
    for line in lines:
        # Look for lines with memory references
        if 'memory reference' in line.lower() or 'mem refs' in line.lower():
            mem_refs.append(line.strip())
        elif '-->' in line and 'VA' in line:
            # Count the word "checks" or "references" patterns
            if 'checks' in line or 'memory' in line:
                mem_refs.append(line.strip())
    return mem_refs

for seed in [0, 1, 2]:
    print(f"\nSeed {seed}:")
    cmd = f"python paging-multilevel-translate.py -s {seed} -c -n 5"
    result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    
    # Print translation lines
    lines = result.stdout.split('\n')
    in_trans = False
    mem_ref_count = 0
    
    for line in lines:
        if 'Virtual Address Trace' in line:
            in_trans = True
        elif in_trans and line.strip():
            if line.strip().startswith('VA'):
                print(f"  {line.strip()}")
                # Count memory accesses from the translation
                # Multilevel requires: 1 for page dir + 1 for page table entry + 1 for data = 3 min
                if 'Invalid' not in line:
                    # Each valid translation needs: 1 PDE lookup + 1 PTE lookup + 1 data fetch = 3
                    mem_ref_count += 3
                else:
                    # Invalid may need fewer checks
                    mem_ref_count += 1

print("\n" + "="*80)
print("QUESTION 3: Cache Behavior Analysis")
print("="*80)
print("""
MEMORY REFERENCES FOR MULTILEVEL PAGE TABLES:

For a 2-level page table lookup:
  1. Read Page Directory Entry (PDE) from page directory
  2. Read Page Table Entry (PTE) from page table
  3. Read the actual data from memory
  = 3 memory accesses total (if valid)

For a 3-level page table lookup:
  1. Read L1 page directory entry
  2. Read L2 page table entry  
  3. Read L3 page table entry
  4. Read the actual data from memory
  = 4 memory accesses total (if valid)

CACHE BEHAVIOR:

LIKELY TO HIT CACHE (fast):
- Page directories and tables are accessed sequentially
- All processes share them (temporal locality)
- OS loads them at startup (likely stays in cache)

LIKELY TO MISS CACHE (slow):
- Large address spaces cause cache misses for page tables
- Different virtual pages point to scattered physical addresses
- Working set causes interference misses

OVERALL: Moderate cache behavior. Page tables benefit from caching,
but not as well as flat linear tables (since multilevel adds 
overhead). This is why real systems use TLB (Translation Lookaside 
Buffer) to cache page table entries!
""")
