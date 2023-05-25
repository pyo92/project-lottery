// 로또 판매 시간 정의
const timerTimes = [
    { //판매 시작까지(일)
        start: {day: 0, time: '00:00:00'},
        end: {day: 0, time: '05:59:59'}
    },
    { //판매 마감까지(일)
        start: {day: 0, time: '06:00:00'},
        end: {day: 0, time: '23:59:59'}
    },
    { //판매 시작까지(월)
        start: {day: 1, time: '00:00:00'},
        end: {day: 1, time: '05:59:59'}
    },
    { //판매 마감까지(월)
        start: {day: 1, time: '06:00:00'},
        end: {day: 1, time: '23:59:59'}
    },
    { //판매 시작까지(화)
        start: {day: 2, time: '00:00:00'},
        end: {day: 2, time: '05:59:59'}
    },
    { //판매 마감까지(화)
        start: {day: 2, time: '06:00:00'},
        end: {day: 2, time: '23:59:59'}
    },
    { //판매 시작까지(수)
        start: {day: 3, time: '00:00:00'},
        end: {day: 3, time: '05:59:59'}
    },
    { //판매 마감까지(수)
        start: {day: 3, time: '06:00:00'},
        end: {day: 3, time: '23:59:59'}
    },
    { //판매 시작까지(목)
        start: {day: 4, time: '00:00:00'},
        end: {day: 4, time: '05:59:59'}
    },
    { //판매 마감까지(목)
        start: {day: 4, time: '06:00:00'},
        end: {day: 4, time: '23:59:59'}
    },
    { //판매 시작까지(금)
        start: {day: 5, time: '00:00:00'},
        end: {day: 5, time: '05:59:59'}
    },
    { //판매 마감까지(금)
        start: {day: 5, time: '06:00:00'},
        end: {day: 5, time: '23:59:59'}
    },
    { //판매 시작까지(토)
        start: {day: 6, time: '00:00:00'},
        end: {day: 6, time: '05:59:59'}
    },
    { //판매 마감까지(토)
        start: {day: 6, time: '06:00:00'},
        end: {day: 6, time: '19:59:59'}
    },
    { //추첨 시작까지(토)
        start: {day: 6, time: '20:00:00'},
        end: {day: 6, time: '20:34:59'}
    },
    { //추첨중(토)
        start: {day: 6, time: '20:35:00'},
        end: {day: 6, time: '20:39:59'}
    },
    { //신규 발행까지
        start: {day: 6, time: '20:40:00'},
        end: {day: 6, time: '23:59:59'}
    },
];

// 로또 관련 타이머 문구 설정
const timerTexts = [
    '금일 판매 시작까지(일)', '금일 판매 마감까지(일)',
    '금일 판매 시작까지(월)', '금일 판매 마감까지(월)',
    '금일 판매 시작까지(화)', '금일 판매 마감까지(화)',
    '금일 판매 시작까지(수)', '금일 판매 마감까지(수)',
    '금일 판매 시작까지(목)', '금일 판매 마감까지(목)',
    '금일 판매 시작까지(금)', '금일 판매 마감까지(금)',
    '금일 판매 시작까지(토)', '금일 판매 마감까지(토)',
    '추첨 시작까지', '추첨중',
    '신규 발행까지'
];

// 로또 관련 타이머 최초 남은 시간 설정
const timerRemains = [
    '05:59:59', '17:59:59',
    '05:59:59', '17:59:59',
    '05:59:59', '17:59:59',
    '05:59:59', '17:59:59',
    '05:59:59', '17:59:59',
    '05:59:59', '17:59:59',
    '05:59:59', '13:59:59',
    '00:34:59', '00:04:59',
    '03:19:59'
];

//동행복권 로그인 및 구매 가능 시간 index
const enterIndexes = [1, 3, 5, 7, 9, 11, 13];

const progressBar = document.querySelector('.progress-bar');
const timerDrawNo = document.querySelector('.timer-draw-no');
const timerText = document.querySelector('.timer-text');
const timerRemain = document.querySelector('.timer-remain');

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
    setInterval(updateTimer, 1000);
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

        const hours = Math.floor((remainingTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((remainingTime % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((remainingTime % (1000 * 60)) / 1000);
        timerRemain.textContent = hours.toString().padStart(2, '0') + ':' + minutes.toString().padStart(2, '0') + ':' + seconds.toString().padStart(2, '0');
    } else {
        currentRangeIndex++;
        currentRangeIndex %= timerTimes.length;

        setProgress(100);

        timerText.textContent = timerTexts[currentRangeIndex];
        timerRemain.textContent = timerRemains[currentRangeIndex];
    }

    drawNo = calcDrawNo(currentDate); //현재 회차 계산
    timerDrawNo.textContent = (currentRangeIndex < 16 ? drawNo : drawNo + 1) + '회'; //현재회차 추첨종료 시, 다음회차번호 표시
}

/**
 * 현재 일시에 따른 회차 계산 처리
 * @param currentDate 현재 일시
 * @returns {number} 현재 회차
 */
function calcDrawNo(currentDate) {
    const startDate = new Date('2002-12-01'); // 시작 날짜 설정
    const millisecondsPerWeek = 7 * 24 * 60 * 60 * 1000; // 1주일에 해당하는 밀리초

    // 현재 날짜와 시작 날짜 사이의 주차 계산
    return Math.ceil((currentDate - startDate) / millisecondsPerWeek);
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
