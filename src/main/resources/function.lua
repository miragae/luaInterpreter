speed = 30
a = 5

function speedUp (x)
    local a = 20
    speed = speed + x + a
end

print(speed) -- initial value (30)

speedUp(10)
print(speed) -- 30 + 10 + 20 = 60

if speed > 45 then
  print("too fast")
  speedUp(-40)
end

print(speed) -- 60 - 40 = 20
print(a) -- 5

print(10-5.0)
print(12.0^2)
print(12^2)
