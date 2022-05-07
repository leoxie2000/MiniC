int fact (int i)
{
int n;

if (i == 0) {
  n = 1;
}
else {
  n = i * fact(i-1);
}

return (n);
}
