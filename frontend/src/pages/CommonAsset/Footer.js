import React from "react";
import "../../css_styles/Footer.css"; // Adjust the path to your CSS file

const Footer = () => {
  return (
    <footer className="site-footer">
      <div className="container">
        <div className="footer-content">
          <div className="copyright-text">
            &copy; 2024 All Rights Reserved by
            <a href="https://alokpandeygzp.github.io/My-Portfolio/" target="_blank" rel="noopener noreferrer">
              {" "}
              Alok Pandey
            </a>.
          </div>
          <ul className="social-icons">
            <li>
              <a className="social-icon facebook" href="https://www.facebook.com/alok2015" target="_blank" rel="noopener noreferrer">
                <i className="fab fa-facebook"></i>
              </a>
            </li>
            <li>
              <a className="social-icon twitter" href="https://x.com/alok_pandey_gzp" target="_blank" rel="noopener noreferrer">
                <i className="fab fa-twitter"></i>
              </a>
            </li>
            <li>
              <a className="social-icon email" href="mailto:alokpandey181@gmail.com">
                <i className="fas fa-envelope"></i>
              </a>
            </li>
            <li>
              <a className="social-icon linkedin" href="https://www.linkedin.com/in/thealokpandey/" target="_blank" rel="noopener noreferrer">
                <i className="fab fa-linkedin"></i>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
