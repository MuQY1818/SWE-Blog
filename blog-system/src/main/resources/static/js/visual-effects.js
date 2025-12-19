/**
 * Weijue's Blog - Visual Effects System (Hacker/Matrix Mode)
 * 
 * Features:
 * 1. Matrix Rain (Green Digital Rain)
 * 2. Glitch Effects
 */

class EffectSystem {
    constructor() {
        this.canvas = document.createElement('canvas');
        this.ctx = this.canvas.getContext('2d');
        this.width = window.innerWidth;
        this.height = window.innerHeight;
        this.mode = 'matrix'; // Force Matrix mode
        this.intensity = 1.0; 
        this.frame = 0;
        this.lastTime = 0;
        
        // Configuration
        this.config = {
            matrixChars: '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZアイウエオカキクケコサシスセソタチツテト',
            colors: {
                matrix: '#0F0'
            }
        };

        this.init();
    }

    init() {
        // Setup Canvas
        this.canvas.style.position = 'fixed';
        this.canvas.style.top = '0';
        this.canvas.style.left = '0';
        this.canvas.style.width = '100%';
        this.canvas.style.height = '100%';
        this.canvas.style.zIndex = '-1';
        this.canvas.style.pointerEvents = 'none'; 
        document.body.appendChild(this.canvas);

        this.resize();
        window.addEventListener('resize', () => this.resize());
        
        this.initMatrix();
        this.animate(0);
    }

    resize() {
        this.width = window.innerWidth;
        this.height = window.innerHeight;
        this.canvas.width = this.width;
        this.canvas.height = this.height;
        this.initMatrix();
    }

    initMatrix() {
        this.matrixColumns = Math.floor(this.width / 20);
        this.matrixDrops = [];
        for (let i = 0; i < this.matrixColumns; i++) {
            this.matrixDrops[i] = Math.random() * -100; // Start above screen
        }
    }

    animate(timeStamp) {
        const deltaTime = timeStamp - this.lastTime;
        this.lastTime = timeStamp;
        this.frame++;

        // Clear with fade effect for trail - darker trail for less visual noise
        this.ctx.fillStyle = 'rgba(10, 15, 20, 0.1)';
        this.ctx.fillRect(0, 0, this.width, this.height);

        this.renderMatrix();

        requestAnimationFrame((t) => this.animate(t));
    }

    renderMatrix() {
        // Softer color and random opacity
        this.ctx.font = '14px "JetBrains Mono", monospace';
        for (let i = 0; i < this.matrixDrops.length; i++) {
            const text = this.config.matrixChars.charAt(Math.floor(Math.random() * this.config.matrixChars.length));
            
            // Random opacity for depth
            const opacity = Math.random() * 0.5 + 0.1;
            this.ctx.fillStyle = `rgba(0, 255, 128, ${opacity})`;
            
            this.ctx.fillText(text, i * 20, this.matrixDrops[i] * 20);

            if (this.matrixDrops[i] * 20 > this.height && Math.random() > 0.985) { // Slower reset
                this.matrixDrops[i] = 0;
            }
            this.matrixDrops[i]++;
        }
    }

    setIntensity(val) {
        this.intensity = val / 100;
        // Adjust matrix opacity or speed based on intensity if needed
        this.config.colors.matrix = `rgba(0, 255, 0, ${this.intensity})`;
    }
}

// Initialize on Load
window.addEventListener('DOMContentLoaded', () => {
    window.effectSystem = new EffectSystem();
});
