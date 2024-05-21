#!/bin/bash

# Generate client1's public/private key pair into private keystore
echo "Generating aaa's public private key pair"
keytool -genkey -alias aaaprivate -keystore aaa.private -storetype PKCS12 -keyalg rsa -dname "CN=aaa" -storepass aaapwd -keypass aaapwd -validity 365

# Generate client2's public/private key pair into private keystore
echo "Generating bbb's public private key pair"
keytool -genkey -alias bbbprivate -keystore bbb.private -storetype PKCS12 -keyalg rsa -dname "CN=bbb" -storepass bbbpwd -keypass bbbpwd -validity 365

# Generate client2's public/private key pair into private keystore
echo "Generating ccc's public private key pair"
keytool -genkey -alias cccprivate -keystore ccc.private -storetype PKCS12 -keyalg rsa -dname "CN=ccc" -storepass cccpwd -keypass cccpwd -validity 365

# Generate server public/private key pair
echo "Generating server public private key pair"
keytool -genkey -alias serverprivate -keystore server.private -storetype PKCS12 -keyalg rsa -dname "CN=localhost" -storepass serverpwd -keypass serverpwd -validity 365

# Export client public key and import it into public keystore
echo "Generating client public key file (aaa, bbb, ccc)"
keytool -export -alias aaaprivate -keystore aaa.private -file temp.key -storepass aaapwd
keytool -import -noprompt -alias aaapublic -keystore client.public -file temp.key -storepass public
rm temp.key

keytool -export -alias bbbprivate -keystore bbb.private -file temp.key -storepass bbbpwd
keytool -import -noprompt -alias bbbpublic -keystore client.public -file temp.key -storepass public
rm temp.key

keytool -export -alias cccprivate -keystore ccc.private -file temp.key -storepass cccpwd
keytool -import -noprompt -alias cccpublic -keystore client.public -file temp.key -storepass public
rm temp.key

# Export server public key and import it into public keystore
echo "Generating server public key file"
keytool -export -alias serverprivate -keystore server.private -file temp.key -storepass serverpwd
keytool -import -noprompt -alias serverpublic -keystore server.public -file temp.key -storepass public
rm temp.key
