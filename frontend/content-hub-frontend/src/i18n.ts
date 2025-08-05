import i18n from "i18next";
import { initReactI18next } from "react-i18next";

// the translations
// (tip move them in a JSON file and import them,
// or even better, manage them separated from your code: https://react.i18next.com/guides/multiple-translation-files)
const resources = {
  en: {
    translation: {}
  },
  kr: {
    translation: {
      info: {
        // 공통
        "login": "로그인",
        "logout": "로그아웃",
        "ani": "애니",
        "comics": "만화",
        "movie": "영화",
        "drama": "드라마",
        "novel": "소설",
        "beforeSearchMessage": "애니/드라마/영화/만화 통합검색",
        "searchPlease": "검색어를 입력하세요",
        "noSearchData": "검색 결과가 없습니다.",
        "viewMore": "전체보기",
        "loginConfirmMsg1": "로그인이 만료되었습니다. 로그인 하시겠습니까?",
        "problemsOccurred" : "문제가 발생했습니다.",
        // 상세화면
        "mediaInfo": "작품 정보",
        "review": "평가&리뷰",
        "recommend": "비슷한 작품",
        "starRating": "별점 선택",
        "overview": "개요",
        "cast": "출연진",
        "crew": "제작진",
        "characters": "캐릭터",
        "loginConfirmMsg2": "로그인이 필요합니다. 로그인 하시겠습니까?",
        // 코멘트
        "deleteConfirmMsg": "코멘트를 삭제하시겠습니까?",
        "requireComment": "코멘트를 입력해 주세요.",
        // 버튼
        "search": "검색",
        "save": "등록",
        "update": "수정",
        "delete": "삭제",
        "refresh": "새로고침",
        "toHome": "홈으로 돌아가기",
        // 필터
        "mediaType": "검색 종류",
        "searchAdultContent": "성인물",
        "include": "포함",
        // 작품 정보
        "genre": "장르",
        "movieRating": "관람 등급",
        "adultContent": "성인용",
        "tvRunningTime": "방영 시간",
        "movieRunningTime": "상영 시간",
        "volumes": "총 권수",
        "onAir": "방영 중",
        "ended": "방영 종료",
        "tvReleaseDate": "방영 시작일",
        "movieReleaseDate": "개봉일",
        "serializeDate": "연재 시작일",
        "seasonNumbers": "시즌 수",
        "season": "시즌",
        "tvReleaseStatus": "방영 상태",
        "movieReleaseStatus": "상영 상태",
        "serializeStatus": "연재 상태",
        "released": "개봉 됨",
        "planned": "개봉 예정",
        "finished": "연재 종료",
        "releasing": "연재 중",
        "homePage": "홈페이지",
        "minutes": "분",
        "volume": "권",
        "notEndedYet": "미완",
        "userStarRating": "유저 평점",
        "ableToWatching": "볼 수 있는 곳",
        // 점검 화면
        "maintenanceTitle": "서비스 점검 중입니다.",
        "maintenanceMessage": "보다 나은 서비스를 제공하기 위해 시스템 점검을 진행하고 있습니다. <br/>점검이 완료되면 정상적으로 서비스 이용이 가능합니다.",
      },
      warn: {
        "searchEmpty": "검색어를 입력해 주세요.",
        "noData": "데이터가 없습니다.",
      },
      error : {
        "problemsOccurred" : "문제가 발생했습니다.",
        "notFound": "페이지를 찾을 수 없습니다.",
        "forbidden": "접근 권한이 없습니다.",
        "networkError": "네트워크 에러가 발생했습니다. 인터넷 연결을 확인해 주세요.",
        "authError": "인증/권한 에러가 발생했습니다.",
        "validationError": "입력값이 유효하지 않습니다.",
        "apiResponseError": "데이터 조회 중 에러가 발생했습니다. 다시 시도해 주세요.",
        "businessError": "잘못된 요청입니다.",
        "serverError": "서버에 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.",
        "unexpectedError": "예기치 못한 에러가 발생했습니다.",
        "badRequestError": "잘못된 요청입니다. 입력값을 확인해 주세요.",
        "systemError": "시스템에 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.",
      }
    },
  }
};

i18n
  .use(initReactI18next) // passes i18n down to react-i18next
  .init({
    resources,
    lng: "kr", // language to use, more information here: https://www.i18next.com/overview/configuration-options#languages-namespaces-resources
    // you can use the i18n.changeLanguage function to change the language manually: https://www.i18next.com/overview/api#changelanguage
    // if you're using a language detector, do not define the lng option

    interpolation: {
      escapeValue: false // react already safes from xss
    }
  });

export default i18n;