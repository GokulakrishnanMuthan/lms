# Backend changes required

This documents the **backend-impacting** parts of the recent frontend work
(barcode/ISBN scanner + Angular 16→20 upgrade). Everything else in the upgrade
(NgRx cleanup, RxJS unsubscribe, Material/CDK alignment, esbuild builder,
control-flow migration) is **frontend-only and needs no backend change**.

API base URL (unchanged): `http://localhost:8080/lms/`

---

## 1. NEW endpoint — return a copy by access number (REQUIRED)

Added to support scanning a book's barcode on the **Book Return** screen.

**Frontend call** (`AuthService.getReturnByAccessno`):

```
GET  /lms/returnByAccessno/{accessno}
```

- `{accessno}` — the access number decoded from the scanned barcode (string).
- Should return the **open / not-yet-returned issue** for that single copy.

### Expected response

The frontend accepts **either** of these shapes (use whichever is easiest):

```jsonc
// Option A — a bare array
[
  {
    "title": "Bhagavad Gita",
    "issueDate": "12-06-2026",       // display only
    "expireDate": "19-06-2026",      // string "dd-MM-yyyy" (parsed client-side)
    "bookreturnDate": null,          // null while still on loan
    "comments": "",
    "accessno": "A12345"
    // ...plus any fields your POST /lms/bookreturn needs to process the return
  }
]
```

```jsonc
// Option B — wrapped (same shape the return-save flow already uses)
{ "bookIssueDetailsList": [ { /* same item as above */ } ] }
```

### Behaviour notes / contract

- **Date format:** `expireDate` must be the string `"dd-MM-yyyy"`. The client splits
  on `-` to build a `Date` (`retunbook.component.ts → parseDate`). This matches the
  format already returned by the existing `returnBook/{devoteId}/{issueDate}` endpoint.
- Return the **same item structure** as the existing return-details endpoint
  (see §3), because the rows feed straight into the existing
  `POST /lms/bookreturn` save call (`{ bookIssueDetailsList: [...] }`).
- **Empty result:** return an empty array (or `{ "bookIssueDetailsList": [] }`).
  The UI then falls back to filtering the already-loaded list — so a 200 with an
  empty list is fine; avoid 404 for "no open issue".
- If the copy is already returned, returning it empty (no open issue) is correct.

> If this endpoint is not implemented yet, the feature still works in a degraded
> mode: the scan falls back to filtering the copies already loaded for the member.
> Implementing it enables scanning a book to pull up its loan directly.

---

## 2. Barcode symbology for printed labels (VERIFY / possibly REQUIRED)

The scanner reads the printed barcode and matches it against `accessno`. The
camera decoder is configured for these symbologies:

```
EAN-13, EAN-8, UPC-A, UPC-E, CODE-128, CODE-39
```

**Action for backend:** confirm what `GET /lms/book/{id}/barcode` actually
generates (it returns an image blob today). For internal access-number labels
that contain letters/mixed characters, **Code 128** is recommended. If the
generator currently uses a symbology *not* in the list above (e.g. PDF417, a 2D
code, or an Interleaved 2-of-5 variant), either:

- switch label generation to **Code 128** (preferred for alphanumeric `accessno`), or
- tell us the symbology so we add it to the scanner's `formatsToSupport` list
  (`src/app/scanner/barcode-scanner-dialog.component.ts`).

**Critical:** the value **encoded in the barcode must equal the `accessno`** stored
on the book record — that string is what the scanner looks up.

---

## 3. Existing endpoints reused by the scanner (NO change needed — for reference)

The scanner reuses data already returned by these endpoints. They only need to
keep including `accessno` on the relevant records:

| Screen | Endpoint | Scanner use | Requirement |
|---|---|---|---|
| New Book Issue | `GET /lms/getAvailableBooks` | match scanned `accessno` → add book to issue list | each book must include `accessno` |
| Book Master (list) | `GET /lms/getAllBooks` | scanned `accessno` drives the access-no filter | `accessno` already present |
| New Book (ISBN) | _none_ | scan only fills the `isbn` field; **no lookup** | no backend change |

> ISBN scanning is **scan-only** — there is no external/internal ISBN metadata
> lookup. The scanned ISBN is just written into the form field. No backend work.

---

## 4. Summary for the backend team

| Item | Action |
|---|---|
| `GET /lms/returnByAccessno/{accessno}` | **Implement** (returns open issue for the copy; see §1). Optional but recommended. |
| Barcode label symbology | **Verify** `book/{id}/barcode` uses EAN/UPC/Code128/Code39; prefer Code128 for `accessno`. |
| `getAvailableBooks` / `getAllBooks` | **Verify** each book includes `accessno`. No code change expected. |
| Everything else (framework upgrade, refactors) | **No backend change.** |
