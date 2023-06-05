const linkShopList = document.querySelector('#link-shop-list');
const linkShopRanking = document.querySelector('#link-shop-ranking');
const linkDrawResult = document.querySelector('#link-draw-result');
const linkPurchaseLotto = document.querySelector('#link-purchase-lotto');
const linkLottoTool = document.querySelector('#link-lotto-tool');
const linkLottoAnalysis = document.querySelector('#link-lotto-analysis');

linkShopList.addEventListener('click', function () {
    window.location.href = '/shop/list';
});

linkShopRanking.addEventListener('click', function () {
    window.location.href = '/shop/ranking';
});

linkDrawResult.addEventListener('click', function () {
    window.location.href = '/L645';
});

linkPurchaseLotto.addEventListener('click', function () {
    window.location.href = '/purchase/dh/login';
});

linkLottoTool.addEventListener('click', function () {
    window.location.href = '/L645/tool';
});

linkLottoAnalysis.addEventListener('click', function () {
    window.location.href = '/L645/analysis';
});

//////////////////////////////

// 로또 관련 타이머 시간 설정
const timerTimes = [
    { //회차 시작까지
        start: {day: 0, time: '00:00:00'},
        end: {day: 0, time: '05:59:59'}
    },
    { //회차 마감까지
        start: {day: 0, time: '06:00:00'},
        end: {day: 6, time: '19:59:59'}
    },
    { //추첨 시작까지
        start: {day: 6, time: '20:00:00'},
        end: {day: 6, time: '20:34:59'}
    },
    { //추첨중
        start: {day: 6, time: '20:35:00'},
        end: {day: 6, time: '20:39:59'}
    },
    { //신규 발행까지
        start: {day: 6, time: '20:40:00'},
        end: {day: 6, time: '23:59:59'}
    }
];

// 로또 관련 타이머 문구 설정
const timerTexts = ['판매 시작까지', '판매 마감까지', '추첨 시작까지', '추첨중', '신규 발행까지'];
const timerRemains = [
    {day: 0, hour: 5, min: 59, sec:59},
    {day: 6, hour: 13, min: 59, sec:59},
    {day: 0, hour: 0, min: 34, sec:59},
    {day: 0, hour: 0, min: 4, sec:59},
    {day: 0, hour: 3, min: 19, sec:59},
];

const progressBar = document.querySelector('.progress-bar');
const timerDrawNo = document.querySelector('.timer-draw-no');
const timerText = document.querySelector('.timer-text');

const timerDay = document.querySelector('.timer-day');
const timerHour = document.querySelector('.timer-hour');
const timerMin = document.querySelector('.timer-min');
const timerSec = document.querySelector('.timer-sec');

const timerProgress = document.querySelector('.timer-progress');

let currentRangeIndex = -1;

let drawNo;

/**
 * DOMContentLoaded event handler
 */
document.addEventListener('DOMContentLoaded', startTimer);

/**
 * 타이머 초기화 관련 처리
 */
function initTimer() {
    // 현재 시간을 `timerTimes`와 비교하여 범위 확인
    const currentDate = new Date();

    for (let i = 0; i < timerTimes.length; i++) {
        const range = timerTimes[i];
        const start = new Date();
        start.setDate(start.getDate() - start.getDay() + range.start.day);
        start.setHours(parseTime(range.start.time).hours, parseTime(range.start.time).minutes, parseTime(range.start.time).seconds);

        const end = new Date();
        end.setDate(end.getDate() - end.getDay() + range.end.day);
        end.setHours(parseTime(range.end.time).hours, parseTime(range.end.time).minutes, parseTime(range.end.time).seconds);

        if (currentDate >= start && currentDate <= end) {
            currentRangeIndex = i;
            break;
        }
    }

    resizeProgressBar(); //화면 크기에 맞게 resize

    $(timerProgress).fadeIn('slow');
}

/**
 * 타이머 동작 처리
 */
function startTimer() {
    initTimer();
    updateTimer();
    setInterval(updateTimer, 1000); //1초 마다 갱신되도록
}

/**
 * 타이머 갱신 처리
 */
function updateTimer() {
    const currentDate = new Date();

    const startDate = new Date();
    startDate.setDate(startDate.getDate() - startDate.getDay() + timerTimes[currentRangeIndex].start.day);
    startDate.setHours(parseTime(timerTimes[currentRangeIndex].start.time).hours, parseTime(timerTimes[currentRangeIndex].start.time).minutes, parseTime(timerTimes[currentRangeIndex].start.time).seconds);

    const endDate = new Date();
    endDate.setDate(endDate.getDate() - endDate.getDay() +  + timerTimes[currentRangeIndex].end.day);
    endDate.setHours(parseTime(timerTimes[currentRangeIndex].end.time).hours, parseTime(timerTimes[currentRangeIndex].end.time).minutes, parseTime(timerTimes[currentRangeIndex].end.time).seconds);

    if (isTimeInRange(currentDate, startDate, endDate)) {
        const totalTime = endDate - startDate;
        const remainingTime = endDate - currentDate;
        const progress = (remainingTime / totalTime) * 100;

        setProgress(progress);

        timerText.textContent = timerTexts[currentRangeIndex];

        const days = Math.floor(remainingTime / (1000 * 60 * 60 * 24));
        const hours = Math.floor((remainingTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((remainingTime % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((remainingTime % (1000 * 60)) / 1000);

        timerDay.textContent = days.toString();
        timerHour.textContent = hours.toString().padStart(2, '0');
        timerMin.textContent = minutes.toString().padStart(2, '0');
        timerSec.textContent = seconds.toString().padStart(2, '0');

    } else {
        currentRangeIndex++;
        currentRangeIndex %= timerTimes.length;

        setProgress(100);

        timerText.textContent = timerTexts[currentRangeIndex];

        timerDay.textContent = timerRemains[currentRangeIndex].day.toString();
        timerHour.textContent = timerRemains[currentRangeIndex].hour.toString().padStart(2, '0');
        timerMin.textContent = timerRemains[currentRangeIndex].min.toString().padStart(2, '0');
        timerSec.textContent = timerRemains[currentRangeIndex].sec.toString().padStart(2, '0');
    }

    timerDrawNo.textContent = calcDrawNo(currentDate) + '회'; //회차 표시
}

/**
 * 현재 일시에 따른 회차 계산 처리
 * @param currentDate 현재 일시
 * @returns {number} 현재 회차
 */
function calcDrawNo(currentDate) {
    const startDate = new Date('2002-11-30'); //시작 날짜 설정
    startDate.setHours(23, 59, 59, 999);

    const millisecondsPerWeek = 7 * 24 * 60 * 60 * 1000; //1주일에 해당하는 밀리초

    //현재 날짜와 시작 날짜 사이의 주차 계산
    let drawNo = Math.ceil((currentDate - startDate) / millisecondsPerWeek);

    const dayOfWeek = currentDate.getDay();
    const hours = currentDate.getHours();
    const minutes = currentDate.getMinutes();

    //추첨 완료(매주 토요일 20시 40분)인 경우 회차를 하나 증가시킴
    if (dayOfWeek === 6 && hours >= 20 && minutes >= 40) {
        drawNo += 1;
    }

    return drawNo;
}

/**
 * timerTimes string parsing 처리
 * @param timeString 시간 string
 * @returns {{hours: number, seconds: number, minutes: number}}
 */
function parseTime(timeString) {
    const timeParts = timeString.split(':');
    return {
        hours: parseInt(timeParts[0]),
        minutes: parseInt(timeParts[1]),
        seconds: parseInt(timeParts[2])
    };
}

/**
 * timerTimes 범위에 현재 시각이 포함되는지 체크
 * @param currentDate 현재 시각
 * @param startDate timerTimes 시작 시각
 * @param endDate timerTimes 종료 시각
 * @returns {boolean} 포함 여부
 */
function isTimeInRange(currentDate, startDate, endDate) {
    return currentDate >= startDate && currentDate <= endDate;
}

/**
 * progress bar 설정
 * @param percent progress bar percent
 */
function setProgress(percent) {
    const circumference = 2 * Math.PI * parseFloat(progressBar.getAttribute('r'));
    const offset = circumference * (1 - percent / 100);
    progressBar.style.strokeDasharray = circumference;
    progressBar.style.strokeDashoffset = offset;

    if (percent < 10) {
        progressBar.style.stroke = '#ff3737';
    } else if (percent < 50) {
        progressBar.style.stroke = '#ffcc00';
    } else {
        progressBar.style.stroke = '#4caf50';
    }
}

const timer = document.querySelector('.timer');

// SVG 요소 선택
const progressBarFill = document.querySelector('.progress-bar-fill');
const progressFill = document.querySelector('.progress-fill');

/**
 * 뷰포트 크기에 따라 SVG 요소 업데이트 처리
 */
function resizeProgressBar() {
    const width = timer.clientWidth; // .timer 요소의 너비 가져오기
    const height = timer.clientHeight; // .timer 요소의 높이 가져오기

    // 너비와 높이에 따라 필요한 대로 cx, cy, r 값을 계산 및 설정
    const cx = width / 2;
    const cy = height / 2;
    const r = Math.min(width, height) / 2 - 20;

    // cx, cy, r 값을 SVG 요소에 설정
    progressFill.setAttribute('cx', cx.toString());
    progressFill.setAttribute('cy', cy.toString());
    progressFill.setAttribute('r', r.toString());

    // progressBarFill 요소의 중심 축 설정
    progressBarFill.setAttribute('cx', cx.toString());
    progressBarFill.setAttribute('cy', cy.toString());
    progressBarFill.setAttribute('r', r.toString());

    // progressBar 요소의 중심 축 설정
    progressBar.setAttribute('cx', cx.toString());
    progressBar.setAttribute('cy', cy.toString());
    progressBar.setAttribute('r', r.toString());
    progressBar.style.transformOrigin = `${cx}px ${cy}px`;
    progressBar.style.transform = `rotate(-90deg)`;
}

window.addEventListener('resize', resizeProgressBar);

//////////////////////////////

/**
 * load event handler
 */
window.addEventListener('load', function () {
    //구매 직후 페이지 넘어온 경우 alert message 표시
    const authAlert = document.querySelector('.alert.alert-danger');
    if (authAlert != null) {
        alert(authAlert.textContent.trim());
        authAlert.remove();
    }
});