<script>
import '@/assets/css/main.css';
import axios from 'axios';

export default {
  name: "HomePage",
  data() {
    return {
      authenticated: false,
      user: {},
      searchWord: null,
      selectedDay: null,
      week: ["월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"],
      todayDay: "",// 오늘 요일을 저장
      contentVisible: true,
    };
  },
  computed: {
    ageInt() {
      return parseInt(this.user.age, 10);
    }
  },
  methods: {
    getTodayDay() {
      const days = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
      const today = new Date();
      this.todayDay = days[today.getDay()]; // 오늘의 요일을 설정
    },
    checkAuthentication() {
      axios.get('http://localhost:8080/logins/loginInfo', {withCredentials: true})
          .then(response => {
            this.user = response.data;
            this.authenticated = true;
          })
          .catch(error => {
            console.error('Failed to fetch user info:', error);
          });
    },
    search() {
      this.contentVisible = false;

    },
    goToSite(url) {
      window.open(url, '_blank'); // 새 창 또는 새 탭에서 URL 열기
    }
  },
  created() {
    this.checkAuthentication(); // 페이지가 로드될 때 로그인 상태를 확인합니다.
    this.getTodayDay(); // 오늘의 요일을 가져옴
  }
}
</script>

<template>
  <!--  {{ process.env.VUE_APP_SERVER_URL}}-->
  <div class="wrap">
    <header class="header">
      <div class="headerName">
        <div class="headerName1">3SAM5OH</div>
        <div class="headerName2">webtoon recommendation</div>
      </div>

      <!-- 검색버튼 -->
      <div class="search-container">
        <input type="text" placeholder="검색어를 입력하세요" v-model="searchWord">
        <button type="button" @click="search" class="search-button"></button>
      </div>


      <div class="logGroup">
        <div v-if="!authenticated" class="loginBtn">
          <a href="http://localhost:8080/oauth2/authorization/naver">
            <img src="@/assets/images/btnG_축약형.png" alt="네이버 로그인"/>
          </a>
        </div>

        <div v-if="authenticated" class="logoutBtn">
          <a href="http://localhost:8080/logout">
            <img src="@/assets/images/btnG_로그아웃.png" alt="로그아웃"/>
          </a>
        </div>
      </div>

    </header>
    <div class="content" v-if="contentVisible">

      <!-- 회원정보 -->
      <div style=" font-size: 22px;
    font-weight: bold;" v-if="Object.keys(user).length <= 0"># 이달의 추천
      </div>
      <div class="memberInfo" v-if="Object.keys(user).length > 0">
        <!-- {{ 나이 user.age }} -->
        <div>
          <div v-if="ageInt >=10 && ageInt <=19 "> # 10대</div>
          <div v-if="ageInt >=20 && ageInt <=29 "> # 20대</div>
          <div v-if="ageInt >=30 && ageInt <=39 "> # 30대</div>
          <div v-if="ageInt >=40 && ageInt <=49 "> # 40대</div>
          <div v-if="ageInt >=50 && ageInt <=59 "> # 50대</div>
        </div>

        <!--성별 {{user.gender}}-->
        <div>
          <div v-if="user.gender == 'F'"> 여성 추천</div>
          <div v-if="user.gender == 'M'"> 남성 추천</div>
        </div>
      </div>
      <div class="webtoonRecommend">

        <div class="image-container">
          <img
              src="https://image-comic.pstatic.net/webtoon/824890/thumbnail/thumbnail_IMAG21_95e7706d-f30f-484a-ae56-46532f7711f6.jpg">
          <label>폭력의 왕</label>
        </div>
        <div class="image-container">
          <img
              src="https://image-comic.pstatic.net/webtoon/824890/thumbnail/thumbnail_IMAG21_95e7706d-f30f-484a-ae56-46532f7711f6.jpg">
          <label>폭력의 왕</label>
        </div>
        <div class="image-container">
          <img
              src="https://image-comic.pstatic.net/webtoon/824890/thumbnail/thumbnail_IMAG21_95e7706d-f30f-484a-ae56-46532f7711f6.jpg">
          <label>폭력의 왕</label>
        </div>
        <div class="image-container">
          <img
              src="https://image-comic.pstatic.net/webtoon/824890/thumbnail/thumbnail_IMAG21_95e7706d-f30f-484a-ae56-46532f7711f6.jpg">
          <label>폭력의 왕</label>
        </div>

      </div>

      <div style="font-size: 22px; font-weight: bold; margin-bottom: 20px">요일별 전체웹툰</div>

      <div class="webtoonList">
        <div
            v-for="(day, index) in week"
            :key="index"
            class="day-container"
            :class="{ 'highlight-today1': todayDay === day }">
          <div class="day-header" :class="{ 'highlight-today2': todayDay === day }">
            {{ day }}
          </div>
          <div class="image-container" @click="goToSite('https://comic.naver.com/webtoon/list?titleId=824890')">
            <img
                src="https://image-comic.pstatic.net/webtoon/824890/thumbnail/thumbnail_IMAG21_95e7706d-f30f-484a-ae56-46532f7711f6.jpg"
            />
            <label>폭력의 왕</label>
          </div>
          <div class="image-container" @click="goToSite('https://comic.naver.com/webtoon/list?titleId=824890')">
            <img
                src="https://image-comic.pstatic.net/webtoon/824890/thumbnail/thumbnail_IMAG21_95e7706d-f30f-484a-ae56-46532f7711f6.jpg"
            />
            <label>폭력의 왕</label>
          </div>
          <div class="image-container" @click="goToSite('https://comic.naver.com/webtoon/list?titleId=824890')">
            <img
                src="https://image-comic.pstatic.net/webtoon/824890/thumbnail/thumbnail_IMAG21_95e7706d-f30f-484a-ae56-46532f7711f6.jpg"
            />
            <label>폭력의 왕</label>
          </div>
          <div class="image-container" @click="goToSite('https://comic.naver.com/webtoon/list?titleId=824890')">
            <img
                src="https://image-comic.pstatic.net/webtoon/824890/thumbnail/thumbnail_IMAG21_95e7706d-f30f-484a-ae56-46532f7711f6.jpg"
            />
            <label>폭력의 왕</label>
          </div>
          <div class="image-container" @click="goToSite('https://comic.naver.com/webtoon/list?titleId=824890')">
            <img
                src="https://image-comic.pstatic.net/webtoon/824890/thumbnail/thumbnail_IMAG21_95e7706d-f30f-484a-ae56-46532f7711f6.jpg"
            />
            <label>폭력의 왕</label>
          </div>
          <div class="image-container" @click="goToSite('https://comic.naver.com/webtoon/list?titleId=824890')">
            <img
                src="https://image-comic.pstatic.net/webtoon/824890/thumbnail/thumbnail_IMAG21_95e7706d-f30f-484a-ae56-46532f7711f6.jpg"
            />
            <label>폭력의 왕</label>
          </div>
        </div>
      </div>
    </div>
    <div class="contentFind" v-else>
      <div style="font-size: 22px; padding: 10px; margin-bottom: 20px; border-bottom:1px solid #d2d2d2">
        <strong>'{{ searchWord }}'</strong> 에 대한 검색결과입니다.
      </div>

      <div class="image-container">
        <div>
          <img
              src="https://image-comic.pstatic.net/webtoon/824890/thumbnail/thumbnail_IMAG21_95e7706d-f30f-484a-ae56-46532f7711f6.jpg">
        </div>
        <div class="webtoonContainer">
          <div class="webtoonTitle">폭력의 왕</div>
          <div class="imageInfo">
            <div>누굴까?</div>
            <div>월요일</div>
            <div>전체이용가</div>
          </div>
        </div>
      </div>
      <div class="image-container">
        <div>
          <img
              src="https://image-comic.pstatic.net/webtoon/824890/thumbnail/thumbnail_IMAG21_95e7706d-f30f-484a-ae56-46532f7711f6.jpg">
        </div>
        <div class="webtoonContainer">
          <div class="webtoonTitle">폭력의 왕</div>
          <div class="imageInfo">
            <div>누굴까?</div>
            <div>월요일</div>
            <div>전체이용가</div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
.highlight-today1 {
  background-color: #c5ffca; /* 오늘 요일의 배경색 변경 */
}

.highlight-today2 {
  background-color: #00dc64; /* 오늘 요일의 배경색 변경 */
  color: #fff;
  font-weight: bold;

}
</style>