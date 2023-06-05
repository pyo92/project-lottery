/**
 * load event handler
 */
window.addEventListener('load', function () {
   //구매 직후 페이지 넘어온 경우 alert message 표시
   const purchaseAlert = document.querySelector('.alert.alert-success');
   if (purchaseAlert != null) {
      alert(purchaseAlert.textContent.trim());
      purchaseAlert.remove();
   }

   //구매 결과가 하나도 존재하지 않는 경우 alert message 표시
   const purchasedNoneAlert = document.querySelector('.alert.alert-danger');
   if (purchasedNoneAlert != null) {
      alert(purchasedNoneAlert.textContent.trim());
      purchasedNoneAlert.remove();
   }
});

/**
 * 회차 selector 변경 시, handler
 */
$('#purchase-draw-no').on('change', function () {
   //회차 선택 시, 해당 회차로 이동
   $('#purchase-result-search-form').submit();
});