# To Run API tests:
1. docker-compose up -d 
2. ./gradlew :clean test

# To Run Load test:
1. docker-compose up -d
2. ./gradlew gatlingRun
3. Report will be in the ```build/reports/gatling```

Test load result:
Machine:
```MacM1 max RAM 32Gb```

### 200 constantUsersPerSec - 60 sec duration
```
---- Global Information --------------------------------------------------------
> request count                                      12000 (OK=12000  KO=0     )
> min response time                                      1 (OK=1      KO=-     )
> max response time                                    205 (OK=205    KO=-     )
> mean response time                                     4 (OK=4      KO=-     )
> std deviation                                         10 (OK=10     KO=-     )
> response time 50th percentile                          3 (OK=3      KO=-     )
> response time 75th percentile                          4 (OK=4      KO=-     )
> response time 95th percentile                          7 (OK=7      KO=-     )
> response time 99th percentile                         49 (OK=49     KO=-     )
> mean requests/sec                                    200 (OK=200    KO=-     )
---- Response Time Distribution ------------------------------------------------
> t < 800 ms                                         12000 (100%)
> 800 ms <= t < 1200 ms                                  0 (  0%)
> t >= 1200 ms                                           0 (  0%)
> failed                                                 0 (  0%)
================================================================================
```

### 500 constantUsersPerSec - 60 sec duration
```
================================================================================
---- Global Information --------------------------------------------------------
> request count                                      30000 (OK=30000  KO=0     )
> min response time                                      1 (OK=1      KO=-     )
> max response time                                   1095 (OK=1095   KO=-     )
> mean response time                                    23 (OK=23     KO=-     )
> std deviation                                         90 (OK=90     KO=-     )
> response time 50th percentile                          4 (OK=4      KO=-     )
> response time 75th percentile                          5 (OK=5      KO=-     )
> response time 95th percentile                         95 (OK=95     KO=-     )
> response time 99th percentile                        529 (OK=529    KO=-     )
> mean requests/sec                                    500 (OK=500    KO=-     )
---- Response Time Distribution ------------------------------------------------
> t < 800 ms                                         29933 (100%)
> 800 ms <= t < 1200 ms                                 67 (  0%)
> t >= 1200 ms                                           0 (  0%)
> failed                                                 0 (  0%)
================================================================================
```

### 1000 constantUsersPerSec - 120 sec duration

```
================================================================================
---- Global Information --------------------------------------------------------
> request count                                     120000 (OK=33607  KO=86393 )
> min response time                                      0 (OK=2      KO=0     )
> max response time                                  33014 (OK=11861  KO=33014 )
> mean response time                                  6741 (OK=126    KO=9314  )
> std deviation                                       9119 (OK=718    KO=9574  )
> response time 50th percentile                        131 (OK=7      KO=10207 )
> response time 75th percentile                      12097 (OK=11     KO=13312 )
> response time 95th percentile                      27843 (OK=649    KO=28539 )
> response time 99th percentile                      30265 (OK=1261   KO=30572 )
> mean requests/sec                                902.256 (OK=252.684 KO=649.571)
---- Response Time Distribution ------------------------------------------------
> t < 800 ms                                         32531 ( 27%)
> 800 ms <= t < 1200 ms                                712 (  1%)
> t >= 1200 ms                                         364 (  0%)
> failed                                             86393 ( 72%)
---- Errors --------------------------------------------------------------------
> j.n.SocketException: Too many open files                        38893 (45,02%)
> j.i.IOException: Premature close                                25904 (29,98%)
> i.n.c.ConnectTimeoutException: connection timed out: localhost  13386 (15,49%)
/127.0.0.1:8081
> j.n.SocketException: Connection reset by peer                    5631 ( 6,52%)
> j.n.SocketException: Resource temporarily unavailable            2499 ( 2,89%)
> i.n.c.ConnectTimeoutException: connection timed out: localhost     80 ( 0,09%)
/0:0:0:0:0:0:0:1:8081
================================================================================
```


Summary
The 95th and 99th percentiles for 200 and 500 concurrent users shows a short response time.
And there are no errors for 200 and 500 concurrent users.

For the 1000 concurrent users,  have a 72% error rate, but for successful request response time is still well, only less than 1% have a response time of more than 1200 ms
As an improvement for 1000 concurrent users load need to add additional machines with load balance and/or implement the sharding mechanism