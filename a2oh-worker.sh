#!/bin/bash
# a2oh-worker.sh — Launch a CC worker for distributed shim implementation
#
# Usage:
#   ./a2oh-worker.sh [WORKER_DIR] [BATCH_SIZE]
#
# Examples:
#   ./a2oh-worker.sh                          # Uses /tmp/a2oh-worker-$$, batch=10
#   ./a2oh-worker.sh /tmp/worker1             # Custom dir, batch=10
#   ./a2oh-worker.sh /tmp/worker1 5           # Custom dir, batch=5
#   TIER=b ./a2oh-worker.sh                   # Work on tier-b issues
#   LOOP=1 ./a2oh-worker.sh                   # Keep picking up work until no todo left

set -euo pipefail

REPO="https://github.com/A2OH/harmony-android-guide.git"
REPO_SLUG="A2OH/harmony-android-guide"
WORKER_DIR="${1:-/tmp/a2oh-worker-$$}"
BATCH="${2:-10}"
TIER="${TIER:-a}"
LOOP="${LOOP:-0}"

echo "=== A2OH Worker ==="
echo "  Dir:   $WORKER_DIR"
echo "  Batch: $BATCH"
echo "  Tier:  $TIER"
echo "  Loop:  $LOOP"
echo ""

# ─── Step 1: Ensure repo is cloned and up to date ────────────────────────────

if [ -d "$WORKER_DIR/.git" ]; then
    echo "[1/3] Repo exists, pulling latest..."
    cd "$WORKER_DIR"
    git fetch origin
    # Reset to origin/main to handle force-push scenarios
    git checkout main 2>/dev/null || git checkout -b main origin/main
    git reset --hard origin/main
else
    echo "[1/3] Cloning repo to $WORKER_DIR..."
    git clone "$REPO" "$WORKER_DIR"
    cd "$WORKER_DIR"
fi

# ─── Step 2: Verify prerequisites ────────────────────────────────────────────

echo "[2/3] Verifying prerequisites..."

# Check Java
if ! command -v javac &>/dev/null; then
    echo "ERROR: javac not found. Need JDK 8+."
    exit 1
fi

# Check gh CLI
if ! gh auth status &>/dev/null; then
    echo "ERROR: gh not authenticated. Run: gh auth login"
    exit 1
fi

# Check test infrastructure
for f in test-apps/run-local-tests.sh test-apps/mock/com/ohos/shim/bridge/OHBridge.java test-apps/02-headless-cli/src/HeadlessTest.java; do
    if [ ! -f "$f" ]; then
        echo "ERROR: Missing $f"
        exit 1
    fi
done

# Check Claude Code
if ! command -v claude &>/dev/null; then
    echo "ERROR: claude CLI not found. Install: npm install -g @anthropic-ai/claude-code"
    exit 1
fi

# ─── Step 3: Verify test baseline ────────────────────────────────────────────

echo "[3/3] Running test baseline..."
BASELINE=$(bash test-apps/run-local-tests.sh headless 2>&1 | grep -E '^Passed:' | head -1 || true)
echo "  Baseline: $BASELINE"

# ─── Step 4: Launch CC in auto mode ──────────────────────────────────────────

PROMPT="Read CLAUDE.md for full instructions. You are a distributed worker.

STEP 1: Claim issues
Run: gh issue list --repo $REPO_SLUG --label todo --label tier-$TIER --limit $BATCH
Pick up to $BATCH issues. For each, claim it:
  gh issue edit <N> --repo $REPO_SLUG --remove-label todo --add-label in-progress

STEP 2: Implement in parallel
Launch parallel agents (one per issue). Each agent:
1. Reads the issue body for the class name and skill file
2. Reads the existing stub at shim/java/...
3. Reads the skill file (skills/<SKILL>.md) for conversion rules
4. Implements real Java logic (replace return null/0/false with working code)
5. For Tier A/B: use pure Java only, no JNI/OHBridge
6. Match AOSP method signatures exactly

STEP 3: Verify
After all agents finish, run: cd test-apps && ./run-local-tests.sh headless
Baseline is 497/502. Must not regress.

STEP 4: Commit and close
For each implemented class:
  git add shim/java/android/<path>/<Class>.java
Commit all together, push to main.
Then close each issue:
  gh issue close <N> --repo $REPO_SLUG --comment 'Implemented and tested'
  gh issue edit <N> --repo $REPO_SLUG --remove-label in-progress --add-label done

IMPORTANT: Do NOT add Co-Authored-By lines to commits."

if [ "$LOOP" = "1" ]; then
    echo ""
    echo "=== LOOP MODE: Will keep picking up work until no todo issues remain ==="
    while true; do
        # Check if there are any todo issues left
        TODO_COUNT=$(gh issue list --repo "$REPO_SLUG" --label todo --label "tier-$TIER" --limit 1 --json number 2>/dev/null | python3 -c "import json,sys; print(len(json.load(sys.stdin)))" 2>/dev/null || echo "0")
        if [ "$TODO_COUNT" = "0" ]; then
            echo "No more tier-$TIER todo issues. Done!"
            break
        fi
        echo ""
        echo "=== Starting CC worker session ($(date)) ==="
        git fetch origin && git reset --hard origin/main
        claude --dangerously-skip-permissions -p "$PROMPT" || true
        echo "=== Session ended, checking for more work... ==="
        sleep 5
    done
else
    echo ""
    echo "=== Launching CC worker ==="
    claude --dangerously-skip-permissions -p "$PROMPT"
fi
