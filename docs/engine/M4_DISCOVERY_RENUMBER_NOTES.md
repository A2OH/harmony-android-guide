# M4_DISCOVERY.md renumber audit (DOC1)

**Date:** 2026-05-12
**Owner:** Builder (documentation hygiene swarm role)
**Scope:** `docs/engine/M4_DISCOVERY.md` only (no source files touched).

## Problem

12+ agents wrote into `M4_DISCOVERY.md` in parallel during the
single-day 2026-05-12 Phase 1 sprint. The numbering conflicted in two
specific places:

1. **Duplicate `## 15.`** — M4-power landed first under §15 (line 1009 of
   the pre-renumber file: "## 15. M4-power landed (2026-05-12 night)").
   Later that night M4a landed and was also written as `## 15. M4a —
   IActivityManager Tier-1 service stub`. Reading order put M4a's
   §15.1-§15.6 *after* M4-PRE4's §16 — so the doc had two §15 blocks
   separated by an unrelated §16 block.

2. **Anomalous `## §28 — M4d + M4e batch`** — the M4d/M4e batch agent
   started its new section header with a literal `§` glyph (`## §28
   — ...`) rather than `## 28. ...`. Cosmetic but inconsistent with
   every other §N header in the file.

## Resolution

### Renumbering map

The duplicate §15 (M4a) was promoted to §17, and every section downstream
shifted +1:

| Pre-DOC1 §   | Milestone (one line)                         | Post-DOC1 § |
|--------------|----------------------------------------------|-------------|
| §1-§11       | W2-discover initial analysis                 | §1-§11 (unchanged) |
| §12          | Post-M4-PRE re-discovery                     | §12 |
| §13          | Post-M4-PRE2 re-discovery                    | §13 |
| §14          | Post-M4-PRE3 re-discovery                    | §14 |
| §15 (first)  | M4-power landed                              | §15 |
| §16          | Post-M4-PRE4 re-discovery                    | §16 |
| §15 (second) | **DUPLICATE** — M4a IActivityManager landed  | **§17** |
| §17          | Post-M4-PRE5 re-discovery                    | §18 |
| §18          | CR2 fail-loud unobserved service methods     | §19 |
| §19          | Post-M4-PRE6 re-discovery                    | §20 |
| §20          | Post-M4-PRE7 re-discovery                    | §21 |
| §21          | CR3 getSystemService binder routing          | §22 |
| §22          | Post-M4-PRE8 re-discovery                    | §23 |
| §23          | M4b WindowManager landed                     | §24 |
| §24          | CR4 layout_inflater process-local routing    | §25 |
| §25          | M5-PRE AudioSystem native stubs              | §26 |
| §26          | Post-M4-PRE9 re-discovery                    | §27 |
| `§28` (anomalous header) | M4d + M4e batch                  | §28 |

Subsection numbers (`### N.M`) were shifted in lockstep with their
parents. Inline cross-references `§N` / `§N.M` where `N >= 18` were
shifted by +1 mechanically (via a single-pass Python script); a few
references inside the renumbered region that pointed to the old §17
(now §18) were manually corrected with explicit "was §17 pre-DOC1
renumber" notes so future readers can trace the rename.

### Anomalous header fix

`## §28 — M4d + M4e batch...` was rewritten as `## 28. M4d + M4e batch...`
to match the rest of the document's header convention.

### Section index added

A new "Section index (post-DOC1 renumber)" table was inserted near the
top of `M4_DISCOVERY.md` (between the Date/Author header and the TL;DR)
mapping every §N to its milestone in one line. The index is the
authoritative lookup for cross-doc references from
`PHASE_1_STATUS.md`, `BINDER_PIVOT_MILESTONES.md`, etc.

An "Out-of-band cross-references" sub-table lists work that has its own
canonical doc and does NOT have a §N here (C1-C5, M1-M3 variants, CR1,
CR5-merged-into-M4-PRE9, D, D2).

## Duplicates resolved: 1

Exactly one section number was duplicated in the pre-DOC1 doc (§15).
Resolution: keep first occurrence as §15 (M4-power, chronologically
earlier — file mtime 2026-05-12 15:39), promote second occurrence to §17
(M4a, file mtime 2026-05-12 15:46), cascade-shift downstream.

No content was deleted — every previously-recorded discovery iteration
and milestone landing is preserved verbatim under its new section
number. Cross-references were updated where ambiguous; the
section-index table at the top provides the single source of truth for
"which milestone is at §N".

## Verification

- Final section count: 29 (sequential, no duplicates).  (§29 — CR5's
  registry wrap arms — landed concurrently with DOC1's renumber pass;
  added to the index alongside §28.)
- Final subsection count: ~200 `### N.M` headers.
- `grep -n "^## [0-9]" M4_DISCOVERY.md` shows §1-§29 in order, no gaps.
- Cross-references (`§N` and `§N.M`) shifted via a one-pass Python
  script; manual spot-checks at: line 1841 (`Per §18.6 (was §17.6
  pre-DOC1 renumber)`), line 1931 (`M4-PRE5 §18.5`), line 1993
  (`validates §16.5 / §18.5 prediction`), line 1532 (`this §18 append
  (was §17 pre-DOC1 renumber)`).
- Companion update: `PHASE_1_STATUS.md` headline table refreshed to
  reference the renumbered §N values (see its §1.3 "Discovery + Tier-1
  service work" table — every row's last column points to the post-DOC1
  §N).

## Person-time

~45 minutes total:
- 10 min audit (read full doc, identify §15 duplicate + §28 anomaly,
  cross-check timestamps to determine chronological order)
- 15 min Python renumber script (initial buggy logic, corrected on
  second pass to use "first-duplicate triggers a single permanent +1
  shift" instead of "every-duplicate-cascades")
- 15 min fix cross-references inside renumbered sections
- 5 min add section index + section-anchor verification

## Files touched by DOC1

- `docs/engine/M4_DISCOVERY.md` — renumbered + section index + minor
  cross-ref fixes
- `docs/engine/PHASE_1_STATUS.md` — replaced headline table with the
  canonical 5-column schema (Milestone | Status | Date | Owner |
  Result-summary | Files-touched); added cumulative summary statistics
  block (§1.5)
- `/home/dspfac/.claude/projects/-home-dspfac-openharmony/memory/project_binder_pivot.md`
  — refreshed artifact paths + cumulative LOC counts to match
  PHASE_1_STATUS.md §5
- `docs/engine/M4_DISCOVERY_RENUMBER_NOTES.md` — this file (NEW)

No source files (`shim/java/*`, `art-latest/*`, `aosp-libbinder-port/*`)
were modified.
