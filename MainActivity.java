package by.mastudio.arrowspin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;

import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    //элементы UI
    private ImageView ivArrowB, ivArrowR;
    private ImageButton ibRB, ibBR, ibRR, ibBB;
    private ImageButton ibOE, ibEO, ibOO, ibEE;
    private ImageButton ibSum1, ibSum2, ibSum3, ibSum4;
    private ImageButton ibBet05, ibBet10, ibBet20, ibBet50;
    private TextView tvCoins, tvCoinsCh, tvArrowBNum, tvArrowRNum, tvSum;

    //внутренние переменные
    private int coins, arrowBNum, arrowRNum;
    private int arrowBDirStart, arrowBDirEnd;
    private int arrowRDirStart, arrowRDirEnd;
    private boolean spinning;

    private int indexBR, indexEO, indexSum, indexBet;

    //СОХРАНЕНИЕ НАСТРОЕК
    //имя файла настроек
    public static final String APP_SETTINGS = "arrow_spin_settings";
    //ключ-параметр
    public static final String APP_SETTINGS_COINS = "coins";
    //
    private SharedPreferences appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appSettings = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);

        //image черной стрелки
        ivArrowB = findViewById(R.id.ivArrow);
        //image красной стрелки
        ivArrowR = findViewById(R.id.ivArrow2);

        //кнопки вариантов красное-черное
        ibRB = findViewById(R.id.ibRB);
        ibBR = findViewById(R.id.ibBR);
        ibRR = findViewById(R.id.ibRR);
        ibBB = findViewById(R.id.ibBB);

        //кнопки вариантов чёт-нечёт
        ibOE = findViewById(R.id.ibOE);
        ibEO = findViewById(R.id.ibEO);
        ibOO = findViewById(R.id.ibOO);
        ibEE = findViewById(R.id.ibEE);

        //кнопки вариантов суммы чисел
        ibSum1 = findViewById(R.id.ibSum1);
        ibSum2 = findViewById(R.id.ibSum2);
        ibSum3 = findViewById(R.id.ibSum3);
        ibSum4 = findViewById(R.id.ibSum4);

        //кнопки величины ставки
        ibBet05 = findViewById(R.id.ibBet05);
        ibBet10 = findViewById(R.id.ibBet10);
        ibBet20 = findViewById(R.id.ibBet20);
        ibBet50 = findViewById(R.id.ibBet50);

        //поля отображения результата розыгрыша
        tvArrowBNum = findViewById(R.id.tvArrowBNum);
        tvArrowRNum = findViewById(R.id.tvArrowRNum);
        tvSum = findViewById(R.id.tvSum);

        //поле отображения текущих монет
        tvCoins = findViewById(R.id.tvCoins);
        tvCoinsCh = findViewById(R.id.tvCoinsCH);

        //инициализация начальных значений
        tvArrowBNum.setText("");
        tvArrowRNum.setText("");
        tvSum.setText("");
        coins = 1000;
        tvCoins.setText(String.format(Locale.UK,"%d", coins));
        tvCoinsCh.setText("");
        arrowBDirStart = 0;
        arrowRDirStart = 0;

        //индексы выбора кнопок
        indexBR = 0;
        indexEO = 0;
        indexSum = 0;
        indexBet = 0;

        //флаг запуска анимации вращения стрелок
        spinning = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //восстанавливаем сохраненные данные
        if (appSettings.contains(APP_SETTINGS_COINS)) {
            // Получаем число из настроек
            coins = appSettings.getInt(APP_SETTINGS_COINS, 1000);
            // Выводим на экран данные из настроек
            tvCoins.setText(String.format(Locale.UK,"%d", coins));
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        //сохраняем данные (coins)
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putInt(APP_SETTINGS_COINS, coins);
        editor.apply();
    }

    @SuppressLint("SetTextI18n")
    public void spinArrow(View view) {
        if (!checkBet()){
            Toast.makeText(this, "Сделайте ставку!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!spinning) {
            Random rnd = new Random();
            arrowBDirEnd = 360 + rnd.nextInt(1800);
            arrowRDirEnd = (360 + rnd.nextInt(1800)) * -1;

            float ivArrowX = ivArrowB.getWidth() / 2f;
            float ivArrowY = ivArrowB.getHeight() / 2f;

            Animation rotationB = new RotateAnimation(arrowBDirStart, arrowBDirEnd, ivArrowX, ivArrowY);
            Animation rotationR = new RotateAnimation(arrowRDirStart, arrowRDirEnd, ivArrowX, ivArrowY);

            final Animation aFadeOutText = AnimationUtils.loadAnimation(this, R.anim.fadeout);
            aFadeOutText.setFillAfter(true);

            rotationB.setDuration(2700);
            rotationR.setDuration(2700);

            rotationB.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    spinning = true;

                    tvArrowBNum.setText("");
                    tvArrowRNum.setText("");
                    tvSum.setText("");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    spinning = false;
                    arrowBNum = arrowBDirStart/10 + 1;
                    arrowRNum = (360 + arrowRDirStart)/10 + 1;

                    if (numIsBlack(arrowBNum)){
                        tvArrowBNum.setTextColor(getResources().getColor(R.color.colorText));
                    } else {
                        tvArrowBNum.setTextColor(getResources().getColor(R.color.colorTextR));
                    }

                    if (numIsBlack(arrowRNum)){
                        tvArrowRNum.setTextColor(getResources().getColor(R.color.colorText));
                    } else {
                        tvArrowRNum.setTextColor(getResources().getColor(R.color.colorTextR));
                    }

                    tvArrowBNum.setText("" + arrowBNum + checkOE(arrowBNum));
                    tvArrowRNum.setText("" + arrowRNum + checkOE(arrowRNum));
                    tvSum.setText(String.format(Locale.UK,"%d", (arrowBNum + arrowRNum)));

                    //расчет выйгрыша
                    int win = indexBet * (-1);
                    int k = 0;
                    if (indexBR == getBetBR()){
                        win = win + indexBet;
                        k++;
                    }
                    if (indexEO == getBetOE()){
                        win = win + indexBet;
                        k++;
                    }
                    if (indexSum == getBetSum()){
                        win = win + indexBet;
                        k++;
                    }
                    if (k > 2){
                        win = win + indexBet * k;
                    }

                    
                    /*String str = "RB: " + getBetBR() + "; OE: " + getBetOE() + "; Sum: " + getBetSum() + "WIN: " + win;
                    Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();*/

                    coins = coins + win;
                    if (win < 0){
                        tvCoinsCh.setTextColor(getResources().getColor(R.color.colorTextR));
                        tvCoinsCh.setText("" + win);
                    } else {
                        tvCoinsCh.setTextColor(getResources().getColor(R.color.colorTextG));
                        tvCoinsCh.setText("+" + win);
                    }

                    tvCoinsCh.startAnimation(aFadeOutText);
                    tvCoins.setText("" + coins);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    //
                }
            });
            //rotation.setInterpolator(new AccelerateInterpolator());
            //rotation.setInterpolator(new DecelerateInterpolator());
            //rotation.setInterpolator(new AnticipateInterpolator());
            //rotation.setInterpolator(new AnticipateOvershootInterpolator());
            //rotation.setInterpolator(new OvershootInterpolator());
            //rotation.setInterpolator(new BounceInterpolator());
            //rotation.setInterpolator(new CycleInterpolator(4));
            //rotation.setInterpolator(new LinearInterpolator());
            rotationB.setFillAfter(true);
            rotationR.setFillAfter(true);

            arrowBDirStart = arrowBDirEnd % 360;
            arrowRDirStart = arrowRDirEnd % 360;

            //tvLast.setText("" + (arrowBDirStart/10 + 1));
            //tvNew.setText("" + ((360 + arrowRDirStart)/10 + 1));

            ivArrowB.startAnimation(rotationB);
            ivArrowR.startAnimation(rotationR);
        }
    }

    public void btnColorClick(View view){
        if (spinning) {
            return;
        }
        ibRB.setBackgroundColor(Color.argb(0,0,0,0));
        ibBR.setBackgroundColor(Color.argb(0,0,0,0));
        ibRR.setBackgroundColor(Color.argb(0,0,0,0));
        ibBB.setBackgroundColor(Color.argb(0,0,0,0));
        view.setBackgroundColor(Color.YELLOW);
        switch (view.getId()){
            case R.id.ibRB:
                indexBR = 1; break;
            case R.id.ibBR:
                indexBR = 2; break;
            case R.id.ibRR:
                indexBR = 3; break;
            case R.id.ibBB:
                indexBR = 4; break;
            default:
                indexBR = 0;
        }
    }

    public void btnOEClick(View view){
        if (spinning) {
            return;
        }
        ibOE.setBackgroundColor(Color.argb(0,0,0,0));
        ibEO.setBackgroundColor(Color.argb(0,0,0,0));
        ibOO.setBackgroundColor(Color.argb(0,0,0,0));
        ibEE.setBackgroundColor(Color.argb(0,0,0,0));
        view.setBackgroundColor(Color.YELLOW);
        switch (view.getId()){
            case R.id.ibOE:
                indexEO = 1; break;
            case R.id.ibEO:
                indexEO = 2; break;
            case R.id.ibOO:
                indexEO = 3; break;
            case R.id.ibEE:
                indexEO = 4; break;
            default:
                indexEO = 0;
        }
    }

    public void btnSumClick(View view){
        if (spinning) {
            return;
        }
        ibSum1.setBackgroundColor(Color.argb(0,0,0,0));
        ibSum2.setBackgroundColor(Color.argb(0,0,0,0));
        ibSum3.setBackgroundColor(Color.argb(0,0,0,0));
        ibSum4.setBackgroundColor(Color.argb(0,0,0,0));
        view.setBackgroundColor(Color.YELLOW);
        switch (view.getId()){
            case R.id.ibSum1:
                indexSum = 1; break;
            case R.id.ibSum2:
                indexSum = 2; break;
            case R.id.ibSum3:
                indexSum = 3; break;
            case R.id.ibSum4:
                indexSum = 4; break;
            default:
                indexSum = 0;
        }
    }

    public void btnBetClick(View view){
        if (spinning) {
            return;
        }
        ibBet05.setBackgroundColor(Color.argb(0,0,0,0));
        ibBet10.setBackgroundColor(Color.argb(0,0,0,0));
        ibBet20.setBackgroundColor(Color.argb(0,0,0,0));
        ibBet50.setBackgroundColor(Color.argb(0,0,0,0));
        view.setBackgroundColor(Color.YELLOW);
        switch (view.getId()){
            case R.id.ibBet05:
                indexBet = 5; break;
            case R.id.ibBet10:
                indexBet = 10; break;
            case R.id.ibBet20:
                indexBet = 20; break;
            case R.id.ibBet50:
                indexBet = 50; break;
            default:
                indexBet = 0;
        }
    }

    public boolean numIsBlack (int num){
        List<Integer> numBlack = Arrays.asList(1, 2, 5, 6, 9, 10, 13, 14, 17, 18, 21, 22, 25, 26, 29, 30, 33, 34);
        return numBlack.contains(num);
    }

    public String checkOE (int num){
        //Если число чётное, то его младший бит = 0
        if((num & 1) == 0){
            return "(II)";
        } else {
            return "(I)";
        }
    }

    public boolean numIsOdd (int num){
        return ((num & 1) == 1);
    }

    public boolean numIsEven (int num){
        return ((num & 1) == 0);
    }

    public boolean checkBet(){
        //boolean flagBR;
        return (indexBR > 0) && (indexEO > 0) && (indexSum > 0) && (indexBet > 0);
    }
    
    public int getBetBR(){
        //красная стрелка - красное. черная стрелка - черное
        if ((!numIsBlack(arrowRNum)) & numIsBlack(arrowBNum)){
            return 1;
        }
        //красная стрелка - черное. черная стрелка - красное
        if (numIsBlack(arrowRNum) & (!numIsBlack(arrowBNum))){
            return 2;
        }
        //красная стрелка - красное. черная стрелка - красное
        if ((!numIsBlack(arrowRNum)) & (!numIsBlack(arrowBNum))){
            return 3;
        }
        //красная стрелка - черное. черная стрелка - черное
        if (numIsBlack(arrowRNum) & numIsBlack(arrowBNum)){
            return 4;
        }
        return 0;
    }

    public int getBetOE(){
        //красная стрелка - odd. черная стрелка - even
        if (numIsOdd(arrowRNum) & numIsEven(arrowBNum)){
            return 1;
        }
        //красная стрелка - even. черная стрелка - odd
        if (numIsEven(arrowRNum) & numIsOdd(arrowBNum)){
            return 2;
        }
        //красная стрелка - odd. черная стрелка - odd
        if (numIsOdd(arrowRNum) & numIsOdd(arrowBNum)){
            return 3;
        }
        //красная стрелка - even. черная стрелка - even
        if (numIsEven(arrowRNum) & numIsEven(arrowBNum)){
            return 4;
        }
        return 0;
    }

    public int getBetSum(){
        int sum = arrowBNum + arrowRNum;
        if ((sum > 0) & (sum < 19)){
            return 1;
        }
        if ((sum > 18) & (sum < 37)){
            return 2;
        }
        if ((sum > 36) & (sum < 55)){
            return 3;
        }
        if ((sum > 54) & (sum < 73)){
            return 4;
        }
        return 0;
    }
}
