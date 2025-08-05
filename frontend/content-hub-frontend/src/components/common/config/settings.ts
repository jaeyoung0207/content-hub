
/**
 * .env에서 설정한 환경변수를 가지고 내부에서 사용할 수 있도록 설정
 */
export const settings = {
  appBackendUrl: import.meta.env.VITE_APP_BACKEND_URL || "http://localhost:8080",
  isBlockingAdultContent: import.meta.env.VITE_BLOCKING_ADULT_CONTENT ? (import.meta.env.VITE_BLOCKING_ADULT_CONTENT === "true") : false,
  isMaintenanceMode: import.meta.env.VITE_MAINTENANCE_MODE ? (import.meta.env.VITE_MAINTENANCE_MODE === "true") : false,
  maintenanceStart: import.meta.env.VITE_MAINTENANCE_START || "2025-07-17 04:00",
  maintenanceEnd: import.meta.env.VITE_MAINTENANCE_END || "2025-07-17 10:00",
  naverRedirectUri: import.meta.env.VITE_NAVER_REDIRECT_URI || "http://localhost:3000/login/naver",
  naverClientId: import.meta.env.VITE_NAVER_CLIENT_ID || "naver_client_id_placeholder",
  kakaoRedirectUri: import.meta.env.VITE_KAKAO_REDIRECT_URI || "http://localhost:3000/login/kakao",
  kakaoClientId: import.meta.env.VITE_KAKAO_CLIENT_ID || "kakao_client_id_placeholder",
};