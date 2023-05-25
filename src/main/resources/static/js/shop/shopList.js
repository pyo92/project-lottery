const shopNameInput = document.getElementById('input-shop-name');

/**
 * 상호명 입력 요소 keydown event handler
 */
shopNameInput.addEventListener('keydown', function (e) {
   if (e.key === 'Enter') {
      searchShopByKeyword(null);
   }
});

/**
 * 상호명 검색 버튼 click event handler
 */
document.getElementById('search-btn').addEventListener('click', searchShopByKeyword);

//////////////////////////////

const sortSelector = document.getElementById('sort-by');

/**
 * 정렬 selector onchange event handler
 */
sortSelector.onchange = function () {
   searchShopByKeyword(sortSelector.value + ',ASC');
};

const directionButton = document.getElementById('direction-btn');

/**
 * 정렬순서 버튼 click event handler
 */
directionButton.addEventListener('click', function () {
   const currentUrl = new URL(window.location.href);
   const sort = currentUrl.searchParams.get('sort');

   if (sort === null) {
      //default = address,ASC 이므로, 정렬순서만 바꿔준다.
      searchShopByKeyword('address,DESC');
   }

   //그 외의 모든 경우는, 정렬순서만 반전시켜준다.
   searchShopByKeyword(sortSelector.value + (sort.endsWith('DESC') ? ',ASC' : ',DESC'));
});

/**
 * 상호명 검색 + 정렬 처리
 * @param sort 정렬 조건
 */
function searchShopByKeyword(sort) {
   const currentUrl = window.location.href;
   const newUrl = new URL(currentUrl);
   newUrl.searchParams.set('page', '0');

   if (sort === null) {
      const paramSort = newUrl.searchParams.get('sort');
      newUrl.searchParams.set('sort', paramSort === null ? '' : paramSort);
   } else {
      newUrl.searchParams.set('sort', sort);
   }

   const paramState1 = newUrl.searchParams.get('state1');
   newUrl.searchParams.set('state1', paramState1 === null ? '' : paramState1);
   const paramState2 = newUrl.searchParams.get('state2');
   newUrl.searchParams.set('state2', paramState2 === null ? '' : paramState2);
   const paramState3 = newUrl.searchParams.get('state3');
   newUrl.searchParams.set('state3', paramState3 === null ? '' : paramState3);

   newUrl.searchParams.set('keyword', shopNameInput.value);
   window.location.href = newUrl.href;
}
