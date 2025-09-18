#!/bin/bash

# 🎬 Movie Search Feature Demo Script 🏴‍☠️
# Ahoy matey! This script demonstrates our treasure hunting capabilities!

echo "🏴‍☠️ Welcome to the Movie Search Treasure Hunt Demo! 🏴‍☠️"
echo "========================================================"
echo ""

BASE_URL="http://localhost:8080"

echo "🎯 Testing Movie Search Endpoints..."
echo ""

echo "1. 📋 Getting all movies (with search form):"
echo "GET $BASE_URL/movies/getMovies"
echo "---"
curl -s "$BASE_URL/movies/getMovies" | head -20
echo ""
echo ""

echo "2. 🔍 Search form (no parameters):"
echo "GET $BASE_URL/movies/search"
echo "---"
curl -s "$BASE_URL/movies/search" | head -20
echo ""
echo ""

echo "3. 🏴‍☠️ Search by name 'Pirate':"
echo "GET $BASE_URL/movies/search?name=Pirate"
echo "---"
curl -s "$BASE_URL/movies/search?name=Pirate" | head -20
echo ""
echo ""

echo "4. 🎭 Search by genre 'Adventure':"
echo "GET $BASE_URL/movies/search?genre=Adventure"
echo "---"
curl -s "$BASE_URL/movies/search?genre=Adventure" | head -20
echo ""
echo ""

echo "5. 🎯 Search by ID '1':"
echo "GET $BASE_URL/movies/search?id=1"
echo "---"
curl -s "$BASE_URL/movies/search?id=1" | head -20
echo ""
echo ""

echo "6. 🎪 Combined search (name + genre):"
echo "GET $BASE_URL/movies/search?name=Treasure&genre=Adventure"
echo "---"
curl -s "$BASE_URL/movies/search?name=Treasure&genre=Adventure" | head -20
echo ""
echo ""

echo "7. 🚫 Search with no results:"
echo "GET $BASE_URL/movies/search?name=NonExistentMovie"
echo "---"
curl -s "$BASE_URL/movies/search?name=NonExistentMovie" | head -20
echo ""
echo ""

echo "8. ⚠️ Search with invalid ID:"
echo "GET $BASE_URL/movies/search?id=-1"
echo "---"
curl -s "$BASE_URL/movies/search?id=-1" | head -20
echo ""
echo ""

echo "🎉 Demo completed! Arrr, what a fine treasure hunting system we've built, matey!"
echo ""
echo "💡 Tips for testing:"
echo "   - Start the application with: mvn spring-boot:run"
echo "   - Open browser to: $BASE_URL/movies/search"
echo "   - Try different search combinations in the web form"
echo "   - Check the logs for search activity"
echo ""
echo "🏴‍☠️ Happy treasure hunting! 🏴‍☠️"