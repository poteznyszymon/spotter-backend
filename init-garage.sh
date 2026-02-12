#!/bin/bash

echo "Czekam na uruchomienie Garage..."
until docker exec spotter-garage-1 /garage status > /dev/null 2>&1; do
  sleep 2
done

NODE_ID=$(docker exec spotter-garage-1 /garage status | grep "ID" -A 1 | tail -n 1 | awk '{print $1}')
echo "Wykryte ID węzła: $NODE_ID"

echo "Konfiguruję layout..."
docker exec spotter-garage-1 /garage layout assign $NODE_ID -z dc1 -c 1G
docker exec spotter-garage-1 /garage layout apply --version 1

echo "Tworzę klucze dostępu..."
docker exec spotter-garage-1 /garage key create spotter-key > keys.txt

echo "Konfiguruję bucket 'avatars'..."
docker exec spotter-garage-1 /garage bucket create avatars
docker exec spotter-garage-1 /garage bucket allow avatars --read --write --key spotter-key
docker exec spotter-garage-1 /garage bucket website --allow avatars

echo "------------------------------------------------------"
echo "GOTOWE! Twoje klucze dostępu znajdziesz w pliku keys.txt"
echo "Wklej je do swojego pliku .env na VPS."
echo "------------------------------------------------------"