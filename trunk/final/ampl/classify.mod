model;

# number of training examples
param l;

# number of input parameters (number of pixels in the handwriting digit images)
param n;

# weight on xi penalty coefficient in primal problem
param C;

# parameters for polynomial machine kernel
param alpha;
param beta;
param delta;

# output vector (1 or -1)
param y { 1..l };

# input data
param x { 1..l, 1..n };

# dual problem variables and simple constraints
var a {1..l} >= 0, <= C;

maximize obj: sum { i in 1..l } a[i] - 0.5 * sum { i in 1..l, j in 1..l } a[i] * a[j] * y[i] * y[j] * ( alpha * ( sum { k in 1..n } x[i,k] * x[j,k] ) + beta ) ^ delta;

s.t. const: sum { i in 1..l } a[i] * y[i] = 0;

option solver loqo;
