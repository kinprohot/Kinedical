import React from 'react';
import HeroSection      from './HeroSection';
import FeatureShowcase  from './FeatureShowcase';
import HowItWorks       from './HowItWorks';
import Statistics       from './Statistics';
import CTAFooter        from './CTAFooter';
import '../../styles/HomePage.css';

const HomePage = ({ onNavigate }) => (
  <div className="home-page" style={{ paddingRight: 44 }}>
    <HeroSection     onNavigate={onNavigate} />
    <FeatureShowcase onNavigate={onNavigate} />
    <HowItWorks />
    <Statistics />
    <CTAFooter       onNavigate={onNavigate} />
  </div>
);

export default HomePage;
