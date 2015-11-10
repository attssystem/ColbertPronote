package pronote.colbert.fliife.com.colbertpronote;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class Intro extends AppIntro2 {

    @Override
    public void init(Bundle savedInstanceState) {
        setCustomTransformer(new FadePageTransformer());
        addSlide(AppIntroFragment.newInstance("Bienvenue !",
                "Cette application vous permettra d'accéder à Pronote.",
                R.drawable.logo_colbert,
                getResources().getColor(R.color.background_material_dark)));
        addSlide(AppIntroFragment.newInstance("Cahier de textes",
                "Accédez à vos devoirs grâce au cahier de textes en ligne.",
                R.drawable.slide_cdt_transparent_background,
                getResources().getColor(R.color.accent_material_light)));
        addSlide(AppIntroFragment.newInstance("Compte",
                "Appuyez sur le bouton gris pour ajouter votre compte.",
                R.drawable.slide_cdt_fab,
                getResources().getColor(R.color.accent_material_light)));
        addSlide(AppIntroFragment.newInstance("Synchroniser",
                "Appuyez sur ce bouton pour synchroniser.",
                R.drawable.ic_sync_white_512dp_2x,
                getResources().getColor(R.color.accent_material_light)));
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        this.finish();
    }

    private static class FadePageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            view.setTranslationX(view.getWidth() * -position);

            if(position <= -1.0F || position >= 1.0F) {
                view.setAlpha(0.0F);
            } else if( position == 0.0F ) {
                view.setAlpha(1.0F);
            } else {
                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                view.setAlpha(1.0F - Math.abs(position));
            }
        }
    }

}
