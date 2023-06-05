/**
 * DOMContentLoaded event handler
 */
document.addEventListener("DOMContentLoaded", function() {
    const activeMenuIco = document.getElementById('active-menu-ico');
    const activeMenuLabel = document.getElementById('active-menu');

    const currentUri = window.location.pathname;

    switch (currentUri) {
        case '/shop':
            const shopName = document.getElementById('shop-name');
            activeMenuIco.classList.add('fa-map-location-dot');
            activeMenuLabel.textContent = '로또판매점 > ' + shopName.textContent;
            break;
        case '/shop/list':
            activeMenuIco.classList.add('fa-map-location-dot');
            activeMenuLabel.textContent = '로또판매점';
            break;
        case '/shop/ranking':
            activeMenuIco.classList.add('fa-trophy');
            activeMenuLabel.textContent = '로또명당';
            break;
        case '/L645':
            const queryParams = new URLSearchParams(window.location.search);
            const drawNo = queryParams.get("drawNo");
            activeMenuIco.classList.add('fa-medal');
            activeMenuLabel.textContent = '로또추첨결과 > ' + drawNo + '회';
            break;
        case '/purchase/dh/login':
            activeMenuIco.classList.add('fa-wallet');
            activeMenuLabel.textContent = '로또구매 > 동행복권로그인';
            break;
        case '/purchase/dh/L645':
            activeMenuIco.classList.add('fa-wallet');
            activeMenuLabel.textContent = '로또구매 > 로또번호선택';
            break;
        case '/purchase/dh/L645/result':
            activeMenuIco.classList.add('fa-wallet');
            activeMenuLabel.textContent = '로또구매 > 로또구매내역';
            break;
        case '/L645/tool':
            activeMenuIco.classList.add('fa-screwdriver-wrench');
            activeMenuLabel.textContent = '로또조합';
            break;
        case '/L645/tool/result':
            activeMenuIco.classList.add('fa-screwdriver-wrench');
            activeMenuLabel.textContent = '로또조합> 로또조합내역';
            break;
        case '/L645/analysis':
            activeMenuIco.classList.add('fa-chart-simple');
            activeMenuLabel.textContent = '로또분석';
            break;
        default:
            activeMenuLabel.textContent = '';
            break;
    }
});

//////////////////////////////

const dhLotteryUrl = 'https://dhlottery.co.kr/gameInfo.do?method=buyLotto&wiselog=C_A_1_3';

/**
 * 모바일 접속 체크
 * @returns {boolean} 모바일 접속 여부
 */
function isMobileDevice() {
    return /iPhone|iPad|iPod|Android/i.test(navigator.userAgent);
}

/**
 * 모바일에서만 로또 구매가 가능하도록 처리
 */
function checkMobileOnPurchaseURL() {
    const currentPath = window.location.pathname;
    const targetPath = '/purchase'; // 모바일 체크를 원하는 특정 URI 경로

    if (currentPath.startsWith(targetPath) && !isMobileDevice()) {
        // '/purchase'로 시작하는 URI이면서 모바일 기기가 아닌 경우에 대한 처리
        alert("PC 에서는 동행복권 사이트를 통해 구매하시기 바랍니다.");
        window.location.href = dhLotteryUrl; // 동행복권 페이지로 리디렉션
    }
}

window.addEventListener('load', checkMobileOnPurchaseURL);
window.addEventListener('resize', checkMobileOnPurchaseURL);

