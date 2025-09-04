import type { Config } from 'tailwindcss';
import scrollbarHide from 'tailwind-scrollbar-hide';

export default {
  // content: [
  //     "./src/**/*.{js,jsx,ts,tsx}",
  // ],
  theme: {
    extend: {},
  },
  plugins: [
    // require("tailwind-scrollbar-hide"),
    scrollbarHide,
  ],
} satisfies Config;
