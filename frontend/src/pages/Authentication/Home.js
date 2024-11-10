import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "../../css_styles/Home.css";
import Header from "../CommonAsset/Header";

// Dynamically require images from the directory
const imageCount = 4; // Total number of images
const backgrounds = [];

for (let i = 1; i <= imageCount; i++) {
  backgrounds.push(require(`../../assets/index_photos/${i}_task.jpg`));
}

const Home = () => {
  const [backgroundIndex, setBackgroundIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setBackgroundIndex((prevIndex) => (prevIndex + 1) % backgrounds.length);
    }, 4000); // Change background every 4 seconds
    return () => clearInterval(interval); // Cleanup interval on unmount
  }, []);

  return (
    <div className="home-wrapper">
      <Header />
      <div
        className="home-container"
        style={{
          backgroundImage: `url(${backgrounds[backgroundIndex]})`,
        }}
      >
        <div className="bg-text">
          <h1>
            <span role="img" aria-label="star">
              ✨
            </span>&nbsp;Organize, Prioritize&nbsp;✅<br/>✨&nbsp;Get Things Done&nbsp;✅
            <span role="img" aria-label="checkmark">
              
            </span>
          </h1>
          <p className="tagline">
            <span role="img" aria-label="rocket">
              🚀
            </span>&nbsp;
            Effortlessly manage tasks, set goals, and boost productivity
            <span role="img" aria-label="bulb">
              💡
            </span>
          </p>
          <p className="body-text">
            <span role="img" aria-label="gear">
              ⚙️
            </span>&nbsp;
            Our platform offers powerful tools to help you stay organized and
            focused. Create tasks lists, set deadlines, track progress, and
            achieve your goals more effectively than ever. Whether you’re managing
            personal tasks or collaborating with teams, we’ve got you covered.
            &nbsp;<span role="img" aria-label="hands">
              🤝
              </span>
          </p>
          <div className="cta-buttons">
            <Link to="/login" className="cta-btn cta-user">
              <span role="img" aria-label="login">
                🔑
              </span>&nbsp;
              Login
            </Link>
            <Link to="/register" className="cta-btn cta-admin">
              <span role="img" aria-label="sign-up">
                📝
              </span>&nbsp;
              Register
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
