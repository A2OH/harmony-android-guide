# V3-W1 commit attribution note

**Date:** 2026-05-16

## Why this file exists

Commit `0742c22d` ("archive: dalvik-port OHOS substrate tree (W13)") inadvertently mixed two concurrent agents' work because their `git add` operations interleaved in the same working tree before either had committed:

* **Agent 42 (W13 archive)** authored the dalvik-port → archive/v2-ohos-substrate/ move (89 files in that commit).
* **Agent 43 (V3-W1 HBC artifact inventory + pull, this task)** had also `git add`-ed the V3-HBC artifacts in the same window (572 files in that commit).

The commit's message describes only agent 42's portion. Agent 43's portion is functionally correct in-tree but its commit attribution is wrong.

## What's actually in `0742c22d`

| Author intent | Path glob | File count |
|---|---|---|
| Agent 42 — W13 archive | `archive/v2-ohos-substrate/dalvik-port/**` | 89 |
| Agent 43 — V3-W1 HBC artifact pull | `westlake-deploy-ohos/v3-hbc/**` | 563 |
| Agent 43 — V3-W1 inventory | `artifacts/v3-hbc-inventory/20260516T065719Z/**` | 4 |
| Agent 43 — V3-W1 smoke test | `artifacts/ohos-mvp/v3-w1-hbc-smoke/20260516T065719Z/**` | 4 |
| Agent 43 — V3-W1 prep doc | `docs/engine/V3-HBC-ARTIFACT-MANIFEST.md` | 1 |
| **Total** | | **661** |

## Where to look for the V3-W1 deliverables

Despite the commit-message attribution issue, the deliverables are intact:

* Pulled HBC artifact tree: `westlake-deploy-ohos/v3-hbc/` (563 files, 412 MB)
* Source inventory: `artifacts/v3-hbc-inventory/20260516T065719Z/manifest.txt`
* SHA-256 checksums: `artifacts/v3-hbc-inventory/20260516T065719Z/checksums.sha256`
* Smoke test record: `artifacts/ohos-mvp/v3-w1-hbc-smoke/20260516T065719Z/README.md`
* Integration prep doc: `docs/engine/V3-HBC-ARTIFACT-MANIFEST.md`

Refer to `V3-HBC-ARTIFACT-MANIFEST.md` as the authoritative summary; this file exists only to clarify git-log archaeology.

## Lesson for future parallel-agent workflows

When multiple agents share a working tree, the second agent must do `git stash --include-untracked` or run inside a worktree before staging, OR coordinate explicitly. The brief warned this case ("agent 42 is writing in parallel — coordinate via cross-references") but the coordination guard was at the content layer, not the git-staging layer.

No rewriting of `0742c22d` was done because the project safety protocol forbids destructive operations without explicit user authorization.
