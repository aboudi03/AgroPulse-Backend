#include <WiFi.h>
#include <HTTPClient.h>
#include <WiFiClientSecure.h> // REQUIRED FOR HTTPS
#include <WebServer.h>
#include "DHT.h"

// =======================================================
// 🌱 DEVICE IDENTIFIER — change this for every ESP
// =======================================================
#define DEVICE_ID "GHOUSE_01"      // UNIQUE ID PER ESP
#define DEVICE_KEY "SECRET123"     // Optional for future security

// =======================================================
// 🌐 WiFi Configuration
// =======================================================
const char* ssid = "amer 2G";
const char* password = "Aboudisaleh123";

// =======================================================
// 🌐 Backend API (RENDER HTTPS)
// =======================================================
const char* BACKEND_URL = "https://agropulse-backend-mxio.onrender.com/api/sensor/add";

// =======================================================
// 🔌 Hardware Pins
// =======================================================
#define SOIL_PIN 34
#define DHTPIN 4
#define DHTTYPE DHT21
DHT dht(DHTPIN, DHTTYPE);

// Trigger server
WebServer server(80);

// =======================================================
// 📡 Connect to WiFi
// =======================================================
void connectWiFi() {
  Serial.println("Connecting to WiFi...");
  WiFi.begin(ssid, password);

  int attempts = 0;
  while (WiFi.status() != WL_CONNECTED && attempts < 30) {
    delay(500);
    Serial.print(".");
    attempts++;
  }

  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\n✅ WiFi Connected!");
    Serial.print("IP Address: ");
    Serial.println(WiFi.localIP());
  } else {
    Serial.println("\n❌ WiFi FAILED. Retrying later...");
  }
}

// =======================================================
// 🌡 Read Sensors + Send to Backend
// =======================================================
void sendReading() {
  // ---- Soil Moisture Averaging ----
  long sum = 0;
  for (int i = 0; i < 10; i++) {
    sum += analogRead(SOIL_PIN);
    delay(10);
  }
  int soilRaw = sum / 10;
  int soilPercent = map(soilRaw, 4095, 0, 0, 100);
  soilPercent = constrain(soilPercent, 0, 100);

  // ---- DHT21 Temperature + Humidity ----
  float humidity = dht.readHumidity();
  float temp = dht.readTemperature();

  if (isnan(humidity) || isnan(temp)) {
    humidity = -1;
    temp = -1;
  }

  Serial.printf("📊 Reading -> Soil: %d%% | Hum: %.1f | Temp: %.1f°C\n",
                soilPercent, humidity, temp);

  // ---- Ensure WiFi ----
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("⚠ Lost WiFi — reconnecting...");
    connectWiFi();
    if (WiFi.status() != WL_CONNECTED) {
      Serial.println("❌ Cannot send data, WiFi still disconnected.");
      return;
    }
  }

  // ---- Prepare JSON ----
  String json = "{";
  json += "\"deviceId\":\"" + String(DEVICE_ID) + "\",";
  json += "\"soil\":" + String(soilPercent) + ",";
  json += "\"humidity\":" + String(humidity) + ",";
  json += "\"temperature\":" + String(temp);
  json += "}";

  Serial.print("🔼 Sending JSON → ");
  Serial.println(json);

  // ---- Send POST (HTTPS) ----
  WiFiClientSecure client;
  client.setInsecure(); // Ignore SSL certificate (needed for free hosting/Let's Encrypt auto-renewal)
  
  HTTPClient http;
  http.begin(client, BACKEND_URL); 
  http.addHeader("Content-Type", "application/json");

  int code = http.POST(json);

  if (code > 0) {
    Serial.printf("✅ Backend Response: %d\n", code);
    String payload = http.getString();
    Serial.println("   Payload: " + payload);
  } else {
    Serial.printf("❌ POST FAILED: %s\n", http.errorToString(code).c_str());
  }

  http.end();
}

// =======================================================
// 🔁 SERIAL COMMAND HANDLER
// =======================================================
void checkSerialCommands() {
  if (Serial.available()) {
    String cmd = Serial.readStringUntil('\n');
    cmd.trim();
    cmd.toLowerCase();

    if (cmd == "send") {
      Serial.println("🖥 Command received: SEND → sending reading...");
      sendReading();
    } else if (cmd.length() > 0) {
      Serial.println("⚠ Unknown command: " + cmd);
    }
  }
}

// =======================================================
// 🔥 Trigger HTTP Endpoint (/trigger)
// =======================================================
void handleTrigger() {
  Serial.println("🔥 HTTP trigger received!");
  sendReading();
  server.send(200, "text/plain", "ESP32 triggered");
}

// =======================================================
// 🔧 Setup
// =======================================================
void setup() {
  Serial.begin(115200);
  delay(1000);

  Serial.println("\n===== 🌱 ESP32 GREENHOUSE NODE (CLOUD) STARTING =====");

  dht.begin();
  connectWiFi();

  // Start trigger server
  server.on("/trigger", handleTrigger);
  server.begin();
  Serial.println("🌐 HTTP Trigger Server running at:");
  Serial.println("    http://" + WiFi.localIP().toString() + "/trigger");

  // Send initial reading once connected
  Serial.println("\n🚀 Converting initial connection... sending first reading!");
  sendReading();
}

// =======================================================
// 🔁 Main Loop
// =======================================================
void loop() {
  server.handleClient();
  checkSerialCommands();
  
  // ⏱ Auto-send removed. Only triggered via HTTP or Serial "send" command.
}
