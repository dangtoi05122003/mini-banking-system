import { useEffect, useState } from "react";
import { DotLottieReact } from "@lottiefiles/dotlottie-react";

import bankingAnimation from "../../assets/animations/Mobile Banking.lottie";
import "../../styles/Home.css";

function getGreeting() {
  const hour = new Date().getHours();
  if (hour >= 5 && hour < 12) return "Chào buổi sáng";
  if (hour >= 12 && hour < 18) return "Chào buổi chiều";
  return "Chào buổi tối";
}

function getWeatherIcon(code) {
  if (code === 0) return "☀️";
  if ([1, 2, 3].includes(code)) return "🌤️";
  if ([45, 48].includes(code)) return "🌫️";
  if ([51, 53, 55, 56, 57].includes(code)) return "🌧️";
  if ([61, 63, 65, 66, 67].includes(code)) return "🌧️";
  if ([71, 73, 75, 77].includes(code)) return "🌨️";
  if ([80, 81, 82].includes(code)) return "🌦️";
  if ([95, 96, 99].includes(code)) return "⛈️";
  return "🌤️";
}

export default function Home() {
  const [weather, setWeather] = useState(null);
  useEffect(() => {
    fetch("https://api.open-meteo.com/v1/forecast?latitude=21.0285&longitude=105.8542&current=temperature_2m,weather_code&timezone=auto")
      .then((response) => response.json())
      .then((data) => {
        setWeather({
          temperature: Math.round(
            data.current.temperature_2m
          ),
          weatherCode: data.current.weather_code,
        });
      })
      .catch((error) => {
        console.error("Weather error:", error);
      });
  }, []);
  return (
    <div className="home">
      <div className="home-background">
        <DotLottieReact src={bankingAnimation} loop autoplay/>
      </div>
      <main className="home-content">
        <div className="welcome-section">
          <div className="welcome-top">
            <div className="weather">
              <div className="weather-icon">
                {weather ? getWeatherIcon(weather.weatherCode): "🌤️"}
              </div>
              <div className="weather-info">
                <span className="weather-temperature">
                  {weather ? `${weather.temperature}°C`: "--°C"}
                </span>
                <span className="weather-location">Hà Nội</span>
              </div>
            </div>
            <h1>{getGreeting()}</h1>
          </div>
          <p>Quý khách đang tìm kiếm gì hôm nay?</p>
          <div className="search-box">
            <span className="search-icon">⌕</span>
            <input type="text" placeholder="Tìm kiếm giao dịch, thanh toán,..."/>
          </div>
        </div>
      </main>
    </div>
  );
}