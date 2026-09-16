# The captcha solver

*[Русская версия](captcha.ru.md)*

The puzzle is six tiles in a 3×2 grid, cut from one picture and shuffled. The answer is the
arrangement that puts the picture back together.

```
0 1 2
3 4 5
```

Each tile is 118×118 with a 5-pixel border; the assembled canvas is 384×256. `CaptchaConstants`
holds those numbers and the grid offsets.

## The idea that makes it work

6! = 720. That is nothing — every arrangement can be tried. There is no search problem here at all.

So the entire task reduces to a **scoring function**: given an assembled candidate, how much does it
look like a real picture? No model, no training data, no labelled captchas. In 2014 without a GPU
that was the only sensible answer, and it is still a good one: the seam scorers below solve the
whole thing in well under a millisecond.

`BaseCaptchaSolver` owns the search — generate the 720 permutations, drop the ones the constraints
reject, score the rest in parallel, keep the best. `Algorithm` is the scoring function. Everything
interesting is in the `Algorithm` implementations.

## Scorers on the ink mask

These came with the original. They binarise each tile — a pixel is ink if all three channels are
below 230 — and score the assembled black-and-white canvas. They are tuned to one source of images,
which is both their strength and their limit.

### DevidedVarianceAlgorithm

The best of the original set, and the one the solvers use.

Split the canvas into five overlapping bands — the two tile rows and the three tile columns — and
score each band by

```
Dx·Dy − cov²
```

the determinant of the covariance matrix of that band's ink points. Geometrically that is the
squared area of the dispersion ellipse. It is small when the points form a thin, oriented
structure, which is exactly what a stroke running unbroken across a tile seam looks like, and large
when the ink is scattered.

Measuring *per band* rather than globally is what makes it discriminate: a global figure cannot tell
a correctly reassembled picture from any other compact blob. This is principal component analysis on
the point cloud, under another name.

Tiles are denoised first: ink is grouped into connected blobs and blobs under 300 pixels are masked
out as texture.

### BorderClusterAlgorithm

The most elaborate. Group each tile's ink into blobs, then decide which are structure:

* under 150 pixels — texture,
* over 1000 — structure,
* in between — judged by the same `Dx·Dy − cov²`, since a stroke is thin and a speckle is round.
  Blobs under 600 pixels get a stricter limit.

What survives is **dilated** by a 12-point structuring element into a fill mask; what does not goes
into a clear mask. Two strokes that ought to continue across a seam then overlap once the tiles are
placed together, and the score is how well the thickened contours meet.

Denoising, morphological dilation, edge-continuity matching — classical computer vision, written by
hand without OpenCV.

### FillAlgorithm

Extend the ink a few pixels into each seam, then flood the background in from a corner and count
what it reaches. A correct assembly closes its contours; a wrong one leaks. The cheapest scorer and
the least precise.

### VarianceAlgorithm

The first attempt: global `Var(x) + Var(y)` of the ink, plus the same along a sheared diagonal scan,
with hand-fitted weights of 10000 and 1250. It is kept because the others are easier to understand
next to it, but it is the weakest — a global variance cannot distinguish a picture from a blob, and
the diagonal term's integer division makes it asymmetric.

## Constraints before search

`EdgeCaptchaSolverBase` is the most pragmatic thing in the package. Which column a tile can sit in is
visible from the tile alone: if its ink never reaches its left edge, nothing can join it there; if it
never reaches the right edge, it belongs in the rightmost column. Four border scans per tile, and
most of the 720 arrangements are gone before anything is scored.

Constraint propagation ahead of search. It is the difference between a solver that works and one
that works in 200 ms.

## Scorers on colour

These are the modern formulation of the problem, added in this repository. They work on colour
directly, so they have no sensitivity threshold to tune, and they compare all 60 ordered tile pairs
once — after which scoring an arrangement is seven table lookups.

### SeamDissimilarityAlgorithm

The standard baseline: compare the touching rows or columns of two tiles directly.

```
D(i, j) = Σ over rows y, over channels c of |i[w-1, y, c] − j[0, y, c]|²
```

Its known weakness is that it rewards two tiles for being uniformly similar rather than for
continuing the same structure, so flat background tiles match everything.

### MahalanobisGradientAlgorithm

The one to use. It asks a better question: not *"are these two edges the same colour?"* but *"is the
step across this seam the kind of step this picture normally takes?"*

For a left-right seam between tiles *i* and *j*:

1. Take the gradient just inside i's right edge, `G(y) = i[w-1,y] − i[w-2,y]`, over every row, and
   summarise it as a mean and a full 3×3 colour covariance.
2. Take the actual step across the seam, `S(y) = j[0,y] − i[w-1,y]`.
3. The cost is the Mahalanobis distance of S from that distribution, summed over rows.

A step is cheap when it matches the direction *and* the spread of the changes i was already making.
Because the covariance is full rather than diagonal, it captures how the three channels move
together, which is what separates a genuine continuation from a coincidental colour match. The
measure is asymmetric, so the pair's cost adds the same quantity computed from j's side looking
back.

This is Gallagher's compatibility measure (CVPR 2012) — the thing that made square-piece jigsaw
solvers work in practice. It needs no training data and no thresholds.

A flat edge produces a singular covariance, so a ridge is added to the diagonal before inversion;
if the determinant still collapses, the code falls back to a scaled identity, which degrades the
measure to plain sums of squared differences rather than producing infinities.

## Performance

Two changes, one much bigger than the other.

**Clustering.** Denoising used commons-math's `DBSCANClusterer` with `minPts = 0`. At that setting
every point is a core point, so DBSCAN reduces exactly to the connected components of the
ε-neighbourhood graph — but the general clusterer still compares every point with every other. A
tile holds up to fifteen thousand ink pixels, so this was on the order of 2.3·10⁸ distance
computations per tile, six tiles per solve. It, not the search, dominated the runtime.

The points are not arbitrary; they sit on an integer grid. `GridClusters` builds the same components
with one union-find pass over a fixed stencil — about forty checks per pixel, some 6·10⁵ operations
per tile. Same output, roughly four hundred times less work. `GridClustersTest` checks the
equivalence against the clusterer it replaced.

**Scoring.** `FastDividedVarianceAlgorithm` computes the band scorer's result without building the
canvas at all. Variance and covariance come from six running sums — count, Σx, Σy, Σx², Σy², Σxy —
which are additive, and a tile at a grid position always falls inside exactly one row band and one
column band because the tiles tile the canvas exactly. So the sums for all 36 (tile, position) pairs
are accumulated once, and scoring an arrangement is a handful of additions per band. The original
rebuilt an 87 000-bit canvas and rescanned it for each of the 720 arrangements.

The search itself now scores in parallel, and no longer walks the ranked list looking for the
identity permutation — that was a measurement harness welded into the production path.

## Using it

From a script:

```javascript
var tiles = PixelBot.solveCaptcha('mgc', x, y, width, height);
```

The type selects the solver. `edge`, `tile`, `texture` and `relic` are the original mask-based ones,
kept because they are tuned to a particular source of images. `seam` and `mgc` are the colour-based
ones; prefer `mgc` for anything new. `CaptchaSolverFactory.produce` is where the mapping lives.

## Adding a scorer

Extend `Algorithm`. Override `calculateWeight(BitSet)` if you want the assembled canvas handed to
you, or `calculateWeight(byte[])` if you can score an arrangement without building it — that is
what the fast and seam scorers do. Lower is better. Then register it in
`CaptchaSolverFactory.produce`, or construct a `SeamCaptchaSolver` around it directly.

`BaseCaptchaSolver.rank()` returns every allowed arrangement scored and sorted, which is what you
want in order to measure a scorer: how often the true arrangement lands in the top *k*.

## Tests

```bash
mvn test
```

`CaptchaSolverTest` cuts a synthetic picture into six tiles, shuffles it and checks that both seam
scorers recover the original order; it also checks that the fast band scorer agrees numerically with
the reference implementation, and that the search keeps arrangements it cannot tell apart.
`MomentStatsTest` checks the statistics against commons-math and pins down the degenerate cases that
used to return `NaN`.

## Known weaknesses

* Every threshold in the mask-based scorers — the 230 ink cutoff, the cluster radius, 150/600/1000,
  15000/40000 — was fitted to one source of images by hand, and there is no way to re-tune them
  without editing code. The colour-based scorers have none of these.
* `VarianceAlgorithm`'s two weights are pure curve-fitting.
* Nothing here handles a captcha that is not exactly six tiles in a 3×2 grid.
