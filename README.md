# Chat-Client aplikacija - ldn7

## Navodila za uporabo

### Zagon

```bash
git clone https://github.com/anzepintar/Chat-Server.git
cd Chat-Server
javac *.java
# v ločenih terminalih
java ChatServer
#
java ChatClient
#
java ChatClient
```

### Javna sporočila

Vsa sporočila so privzeto javna.

### Privatna sporočila

Privatna sporočila se pošilja, tako da se omeni uporabnika. Na primer:

`@ime Vsebina privatnega sproročila`


## Specifikacija formata kodiranja sporočil

### aMessage - sproročilo za nastavitev imena (client -> server)
`{a}{čas}{željeno ime}`
### jMessage - javno spročilo
`{j}{čas}{pošiljatelj}{sporočilo}`
### zMessage - zasebno spročilo
`{z}{čas}{pošiljatelj}{prejemnik}{sporočilo}`
### sMessage - sistemsko sporočilo o napaki (server -> client)
`{s}{čas}{napaka}`

Vsako polje je posamično spremenjeno v Base64, polja so med sabo ločena s podčrtajem.
Za to skrbi razred [EncoderDecoder](EncoderDecoder.java)


## Prikaz funkcionalnosti

User 1

```bash
######  ChatClient  ######
[client] set name: aa
[client] connecting to chat server ...
[client] connected
[client] your name is now aa.
živjo
11:03:41 <@aa> živjo
11:03:44 <@bb> živjo
11:03:48 <@cc> živjo
@bb dajva se pogovarjat brez cc
11:04:21 <@bb> (zasebno) ok
11:04:44 <@cc> o čem se zasebno pogovarjata, jaz nič ne vidim
o ničemer
11:04:57 <@aa> o ničemer
11:05:00 <@bb> o ničemer
@dd še ti mi kaj povej brez da cc vidi
[server]  user dd does not exist
```

User 2

```bash
######  ChatClient  ######
[client] set name: bb
[client] connecting to chat server ...
[client] connected
[client] your name is now bb.
11:03:41 <@aa> živjo
živjo
11:03:44 <@bb> živjo
11:03:48 <@cc> živjo
11:04:00 <@aa> (zasebno) dajva se pogovarjat brez cc
@aa ok
11:04:44 <@cc> o čem se zasebno pogovarjata, jaz nič ne vidim
11:04:57 <@aa> o ničemer
o ničemer
11:05:00 <@bb> nič
```

User 3

```bash
######  ChatClient  ######
[client] set name: cc
[client] connecting to chat server ...
[client] connected
[client] your name is now cc.
11:03:41 <@aa> živjo
11:03:44 <@bb> živjo
živjo
11:03:48 <@cc> živjo
o čem se zasebno pogovarjata, jaz nič ne vidim
11:04:44 <@cc> o čem se zasebno pogovarjata, jaz nič ne vidim
11:04:57 <@aa> o ničemer
11:05:00 <@bb> o ničemer
```
