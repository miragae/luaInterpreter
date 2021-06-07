a = {}     -- create a table and store its reference in `a'
k = "x"
a[k] = 10       -- new entry, with key="x" and value=10
a[20] = "great"  -- new entry, with key=20 and value="great"
print(a["x"])    --> 10
k = 20
print(a[k])      --> "great"
a["x"] = a["x"] + 1     -- increments entry "x"
print(a["x"])    --> 11

print("=====================")

a = {}
a["x"] = 10
b = a      -- `b' refers to the same table as `a'
print(b["x"])  --> 10
b["x"] = 20
print(a["x"])  --> 20

print("=====================")

a.x = 10                    -- same as a["x"] = 10
print(a.x)                  -- same as print(a["x"])
print(a.y)                  -- same as print(a["y"])

print("=====================")

a = {}
x = "y"
a[x] = 10                 -- put 10 in field "y"
print(a[x])   --> 10      -- value of field "y"
print(a.x)    --> nil     -- value of field "x" (undefined)
print(a.y)    --> 10      -- value of field "y"

print("=====================")

i = 10; j = "10"; k = "+10"
a = {}
a[i] = "one value"
a[j] = "another value"
a[k] = "yet another value"

print(a[i])            --> one value
print(a[j])            --> another value
print(a[k])            --> yet another value