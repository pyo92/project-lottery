/**
 * load event handler
 */
window.addEventListener('load', function () {
   //조합 결과가 하나도 존재하지 않는 경우 alert message 표시
   const combinationNoneAlert = document.querySelector('.alert.alert-danger');
   if (combinationNoneAlert != null) {
      alert(combinationNoneAlert.textContent.trim());
      combinationNoneAlert.remove();
   }
});

//////////////////////////////

/**
 * 회차 selector 변경 시, handler
 */
$('#combination-draw-no').on('change', function () {
   //회차 선택 시, 해당 회차로 이동
   $('#combination-result-search-form').submit();
});