#!/usr/bin/env python3

import argparse
import random

CYLINDER_MIN = 0
CYLINDER_MAX = 4999
REQUEST_COUNT = 1000
def validate_head_position(value):
    head = int(value)
    if head < CYLINDER_MIN or head > CYLINDER_MAX:
        raise argparse.ArgumentTypeError(
            f"Initial head position must be in [{CYLINDER_MIN}, {CYLINDER_MAX}]")
    return head
def generate_requests():
    return [random.randint(CYLINDER_MIN, CYLINDER_MAX) for _ in range(REQUEST_COUNT)]
def fcfs_total_movement(initial_head, requests):
    movement = 0
    head = initial_head
    for request in requests:
        movement += abs(request - head)
        head = request
    return movement
def scan_total_movement(initial_head, requests):
    lower = sorted([r for r in requests if r < initial_head])
    upper = sorted([r for r in requests if r >= initial_head])
    movement = 0
    head = initial_head
    for request in upper:
        movement += abs(request - head)
        head = request
    if lower:
        movement += abs(CYLINDER_MAX - head)
        head = CYLINDER_MAX
        for request in reversed(lower):
            movement += abs(request - head)
            head = request
    return movement
def cscan_total_movement(initial_head, requests):
    lower = sorted([r for r in requests if r < initial_head])
    upper = sorted([r for r in requests if r >= initial_head])
    movement = 0
    head = initial_head
    for request in upper:
        movement += abs(request - head)
        head = request
    if lower:
        movement += abs(CYLINDER_MAX - head)
        movement += CYLINDER_MAX - CYLINDER_MIN
        head = CYLINDER_MIN
        for request in lower:
            movement += abs(request - head)
            head = request
    return movement
def main():
    parser = argparse.ArgumentParser(
        description="Simulate FCFS, SCAN, and C-SCAN disk scheduling on 5000 cylinders.")
    parser.add_argument(
        "initial_head",
        type=validate_head_position,
        help="Initial disk head position (0-4999).",)
    args = parser.parse_args()
    requests = generate_requests()
    fcfs = fcfs_total_movement(args.initial_head, requests)
    scan = scan_total_movement(args.initial_head, requests)
    cscan = cscan_total_movement(args.initial_head, requests)
    print(f"FCFS total head movement: {fcfs}")
    print(f"SCAN total head movement: {scan}")
    print(f"C-SCAN total head movement: {cscan}")
if __name__ == "__main__":
    main()