model;

# number of training examples
param l;

# number of input parameters (number of pixels in the handwriting digit images)
param n;

# weight on xi penalty coefficient in primal problem
param C;

# parameters for radial basis function kernel
param gamma;

# output vector (1 or -1)
param y { 1..l };

# input data
param x { 1..l, 1..n };

# dual problem variables and simple constraints
var a {1..l} >= 0, <= C;

maximize obj: sum { i in 1..l } a[i] - 0.5 * sum { i in 1..l, j in 1..l } a[i] * a[j] * y[i] * y[j] * exp( -gamma * ( sum { k in i..n } ( x[i,k] - x[j,k] )^2 ) );

s.t. const: sum { i in 1..l } a[i] * y[i] = 0;

option solver loqo;
