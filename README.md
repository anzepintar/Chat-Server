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
#
java ChatClient
```
### Testni podatki

```bash
Client 1:
    Lokacija ključa: keys/aaa.private
    Geslo ključa: aaapwd

Client 2:
    Lokacija ključa: keys/bbb.private
    Geslo ključa: bbbpwd

Client 3:
    Lokacija ključa: keys/ccc.private
    Geslo ključa: cccpwd
```

### Prijava

Za prijavo uporabnik vnese lokacijo do svojega zasebnega ključa iz katerega se tudi pridobi njegovo uporabniško ime.
Pri prijavi mora vnesti tudi geslo njegovega zasebnega ključa.

### Javna sporočila

Vsa sporočila so privzeto javna.

### Privatna sporočila

Privatna sporočila se pošilja, tako da se omeni uporabnika. Na primer:

`@ime Vsebina privatnega sproročila`


## Specifikacija formata kodiranja sporočil

### jMessage - javno spročilo
`{j}{čas}{sporočilo}`
### zMessage - zasebno spročilo
`{z}{čas}{pošiljatelj}{prejemnik}{sporočilo}`

Polje pošiljatelj je obvezno samo pri pošiljanju sporočila s strežnika na odjemalca.
Ime pošiljatelja se pridobi iz ključa uporabnika.

### sMessage - sistemsko sporočilo o napaki (server -> client)
`{s}{čas}{napaka}`

Vsako polje je posamično spremenjeno v Base64, polja so med sabo ločena s podčrtajem.
Za to skrbi razred [EncoderDecoder](EncoderDecoder.java)


## Prikaz funkcionalnosti

User 1

```bash
######  ChatClient  ######
[client] relative certificate path, e.g. keys/mycert.private: keys/aaa.private
[client] keystore passphrase: aaapwd
[client] connecting to chat server ...
[client] connected
to je javno sporočilo poslano z aaa
22:07:59 <@aaa> to je javno sporočilo poslano z aaa
@bbb to je zasebno sporočilo poslano z aaa, namenjeno za bbb
22:08:59 <@bbb> (zasebno) to je zasebno sporočilo poslano z bbb, namenjeno aaa
22:09:25 <@ccc> Jaz sem ccc in spet ne vidim nobenih zasebnih pogovorov
```

User 2

```bash
######  ChatClient  ######
[client] relative certificate path, e.g. keys/mycert.private: keys/aaa.private
[client] keystore passphrase: aaapwd
[client] connecting to chat server ...
[client] connected
to je javno sporočilo poslano z aaa
22:07:59 <@aaa> to je javno sporočilo poslano z aaa
@bbb to je zasebno sporočilo poslano z aaa, namenjeno za bbb
22:08:59 <@bbb> (zasebno) to je zasebno sporočilo poslano z bbb, namenjeno aaa
22:09:25 <@ccc> Jaz sem ccc in spet ne vidim nobenih zasebnih pogovorov
```

User 3

```bash
######  ChatClient  ######
[client] relative certificate path, e.g. keys/mycert.private: keys/aaa.private
[client] keystore passphrase: aaapwd
[client] connecting to chat server ...
[client] connected
to je javno sporočilo poslano z aaa
22:07:59 <@aaa> to je javno sporočilo poslano z aaa
@bbb to je zasebno sporočilo poslano z aaa, namenjeno za bbb
22:08:59 <@bbb> (zasebno) to je zasebno sporočilo poslano z bbb, namenjeno aaa
22:09:25 <@ccc> Jaz sem ccc in spet ne vidim nobenih zasebnih pogovorov
```
