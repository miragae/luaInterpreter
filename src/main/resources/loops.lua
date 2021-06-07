-- do
print("do")

a = 10
b = 15

do
    local a = 20
    print(a.." "..b)
end
print("finished do")

print(a)

-- while
print("while")

i = 5

while(i > 0)
do
    print(i)
    i = i - 1
end

print("finished while")

-- break
print("break")

while(true)
do
    print("should print only once")
    break
    print("should not print")
end
print("finished break")

-- repeat until
print("repeat until")

a = {"a", "b", "c", "d", "e"}
i = 0

repeat
    i = i + 1
    print(a[i])
until (a[i] == "d")

print("finished repeat until")

-- for
print("for")

for i = 2, 1 -- default step = 1
do
    print("should not enter")
end

for i = 10, 1, -1
do
    print(i)
    i = i - 1 -- print only even
end

print("finished for")
