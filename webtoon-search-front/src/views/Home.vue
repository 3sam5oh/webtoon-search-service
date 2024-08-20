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
      webtoonList: [],
    };
  },
  methods: {
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
      console.log(this.searchWord);
      axios.get('http://localhost:8080/webtoons/search', {
        params: {
          query: this.searchWord  // 서버로 검색어를 query 파라미터로 전송
        }
      })
          .then(response => {
            // 전체 응답 데이터 구조를 출력하여 확인
            console.log('Inner Data:', response.data.data);

            // 데이터가 있는지 확인하고 webtoonList에 저장
            if (response.data && response.data.data && Array.isArray(response.data.data.webtoons)) {
              this.webtoonList = response.data.data.webtoons;
            } else {
              console.error('Invalid data structure');
              this.webtoonList = [];  // 빈 배열로 초기화
            }

            console.log('Webtoon List:', this.webtoonList);
          })
          .catch(error => {
            console.error('Failed to fetch search info:', error);
            this.webtoonList = [];  // 오류 발생 시 빈 배열로 초기화
          });
    }
  },
  created() {
    this.checkAuthentication(); // 페이지가 로드될 때 로그인 상태를 확인합니다.
  }
}
</script>

<template>
  <!--  {{ process.env.VUE_APP_SERVER_URL}}-->
  <div class="wrap">
    <header class="header">
      <div class="headerName">
        <a>
          <div class="headerName1">3SAM5OH</div>
          <div class="headerName2">webtoon recommendation</div>
        </a>
      </div>

      <!-- 검색버튼 -->
      <div class="search-container">
        <form @submit.prevent="search">  <!-- submit 시 search 메서드 호출 -->
          <input
              type="text"
              placeholder="검색어를 입력하세요"
              v-model="searchWord">
          <button type="submit" class="search-button"></button>
        </form>
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
    <body>

    <div class="contentFind">
      <div v-if="webtoonList.length > 0">
        <div v-for="webtoon in webtoonList" :key="webtoon.id" class="image-container">
          <div>
            <!-- thumbnail이 존재하고 요소가 있을 때만 이미지를 렌더링 -->
            <img v-if="webtoon.thumbnail && webtoon.thumbnail.length > 0" :src="webtoon.thumbnail[0]" alt="웹툰 이미지">
            <img v-else src="@/assets/images/noimage.png" alt="기본 이미지">  <!-- 기본 이미지 설정 -->
          </div>
          <div class="webtoonContainer">
            <div class="webtoonTitle">{{ webtoon.title }}</div>
            <div class="imageInfo">
              <div> {{ webtoon.provider }} </div>
              <div>{{ webtoon.authors.join(', ') }}</div>  <!-- 작가 리스트를 쉼표로 구분 -->
              <div>{{ webtoon.ageGrade === 0 ? '전체 이용가' : webtoon.ageGrade + '세 이용가' }}</div>
              <div v-if="webtoon.end">완결</div>
              <div v-else>연재 중</div>
            </div>
            <a :href="webtoon.url" target="_blank" style="font-size: 16px; font-weight: bold">웹툰 보러가기</a>
          </div>
        </div>
      </div>
      <div class="contentNotFind" v-else>
        <p>검색 결과가 없습니다.</p>
      </div>
    </div>
    </body>

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