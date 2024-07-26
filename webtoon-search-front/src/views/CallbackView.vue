<template>
  <div>
    <h1>로그인 성공</h1>
    <p>네이버 로그인이 성공적으로 완료되었습니다.</p>
    <div v-if="users">
      <p><strong>이메일:</strong> {{ users.email }}</p>
      <p><strong>성별:</strong> {{ users.gender }}</p>
      <p><strong>아이디:</strong> {{ users.id }}</p>
      <p><strong>나이:</strong> {{ users.age }}</p>
      <p><strong>닉네임:</strong> {{ users.nickname }}</p>
    </div>
    <div v-else>
      <p>사용자 정보를 불러오는 중...</p>
    </div>
  </div>
</template>

<script>
/* global naver */
import axios from 'axios';

export default {
  name: "CallbackView",
  data() {
    return {
      users: null
    };
  },
  mounted() {
    const naverLogin = new naver.LoginWithNaverId({
      clientId: "Oi2P9dITH68ua3Pz2MSe",
      callbackUrl: "http://localhost:8080/callback",
      // isPopup: true
    });

    naverLogin.init();
    naverLogin.getLoginStatus(status => {
      if (status) {
        const user = naverLogin.user;
        this.users = {
          email: user.email,
          gender: user.gender,
          id: user.id,
          age: user.age,
          nickname: user.nickname
        };

        axios.post('http://localhost:8080/api/oauth2/naver', this.users)
            .then(response => {
              console.log('User data saved:', response.data);
            })
            .catch(error => {
              console.error('Error saving user data:', error);
            });
      } else {
        console.log("로그인 실패");
      }
    });
  }
};
</script>

<style scoped>
h1 {
  color: #4CAF50;
}
</style>
