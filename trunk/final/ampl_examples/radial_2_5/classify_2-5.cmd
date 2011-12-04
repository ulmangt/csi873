reset;
model classify_radial.mod;
data classify_2-5.dat;
solve;
display a > tmp.out;
