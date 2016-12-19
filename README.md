# Mobile Application Lab: Quartett App

## Git Workflow

Es wird ein einfaches Git Flow Modell mit Reviews verwendet.

Ablauf:

1. Issue für Feature erstellen
2. Branch für Feature erstellen
3. Feature auf eigenem Branch entwickeln
4. Issues des Features schließen
5. Codereview
6. Featurebranch in "develop" mergen

Sind einige Feature zu "develop" hinzugefügt worden wird "develop" in "master"
gemerged. Die Version auf "master" entspricht einem stabilen Release der App.


## Code Style

Es wird der [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
verwendet. Zusätzlich wird nach jedem Methodenkopf eine Leerzeile eingefügt.

**Falsch:**
```java
public void do(int a) {
    doStuff()
    doMoreStuff()
    doEvenMoreStuff()
}
```

**Richtig:**
```java
public void do(int a) {

    doStuff()
    doMoreStuff()
    doEvenMoreStuff()
}
```

Bei Methoden deren Rumpf nur eine Zeile umfasst kann die zusätzliche Leerzeile
weggelassen werden.

**Richtig:**
```java
public void do(int a) {
    doStuff()
}
```

**Richtig:**
```java
public void do(int a) {

    doStuff()
}
```


## Model-View-Presenter

Jedes Feature wird in einem eigenen Package mit dem **Model-View-Presenter
Pattern** entwickelt.

Jedes Feature Package enthält folgenden Klassen:

- *FeatureView*: ein Fragment, implementiert View Interface
- *FeaturePresenter*: eine Klasse, implementiert Presenter Interface
- *FeatureActivity*: eine Unterklasse von Activity, erzeugt und verbindet
  *Komponenten*, implementiert Interactor Interface
- *FeatureContract*: ein Interface, beinhaltet Presenter, View und
  Interactor als innere Interfaces

Das Model ist nicht enthalten, da es **nicht** für jedes Feature neu implementiert
wird. Die Klassen des Package "data" stellen das Model da und können global
verwendet werden. Allerdings sollte der Zugriff auf diese Klassen entweder in
*FeatureActivity* oder in *FeaturePresenter* aber **nicht** in *FeatureView* geschehen.

Das *Presenter* Interface und das *View* Interface müssen Untertypen von
*util.BasePresenter* bzw. *util.BaseView* sein.

Das *Interactor* Interface kann vom Presenter verwendet werden um auf Funktionalität
der Android Activity zu zugreifen.

Jede Komponente kann den Activity Context im Konstruktor als Parameter erhalten.

---

Bei Unklarheiten an das
[todo-mvp](https://github.com/googlesamples/android-architecture/tree/todo-mvp/)
Beispiel halten.


## Inversion of Control

Objekte sollten benötigte Komponenten **nicht** selbst erzeugen.
Stattdessen sollten Abhängigkeiten im Konstruktor oder über Setter übergeben
werden.

**Falsch:**
```java
class Presenter {

    View view = new View();
    Model model = new Model();
    Other other = new Other();

    public Presenter() {}

    ...
}
```

**Richtig:**
```java
class Presenter(View view, Model model, Other other) {

    View view;
    Model model;
    Other other;

    public Presenter(View view, Model model, Other other) {

        this.view = view;
        this.model = model;
        this.other = other;
    }

    ...
}
```

**Richtig:**
```java
class Presenter {

    View view;
    Model model;
    Other other;

    public setView(View view) {
        this.view = view;
    }

    public setModel(Model model) {
        this.model = model;
    }

    public setOther(Other other) {
        this.other = other;
    }

    public Presenter() {}

    ...
}
```

Meist werden die benötigen Komponenten in der Activity eines Features erzeugt
und dann über Konstruktoren oder Setter mit anderen Komponenten verbunden.
Insbesondere wird die View dem Presenter im Konstruktor übergeben. Die View
erhält den Presenter über setPresenter(...) aus dem BaseView Interface.


## Ressourcen

Der Name einer Ressource wird vollständig klein geschrieben.
Es werden Unterstriche als Trennzeichen verwendet.

**Falsch:**
```
myGraphic.png
SomeString
ThIsIsSoMeReS
```

**Richtig:**
```
my_graphic.png
some_string
this_is_some_res
```

## Icons

Soweit möglich werden nur die offiziellen
[Material Icons](https://material.io/icons/) verwendet.
Alle Icons sollten in 48dp heruntergeladen werden. Es wird jeweils nur das Icon
mit der höchsten Auflösung (aus dem drawable-xxxhdpi Ordner) verwendet. Alle
Icons werden direkt in "res/drawable" gespeichert. Es werden keine weiteren
Versionen in unterschiedlichen Auflösungen gespeichert.


## Strings

Alle Strings, die in GUIs verwendet werden, werden in "res/values/strings.xml
definiert und nur über "R.string.name" referenziert.

**Falsch:**
```java
start_button.setText("Press me!")
```

**Richtig:**
```java
start_button.setText(R.string.start_button);
```

Die Einträge in strings.xml werden nach Package gruppiert und jede
Gruppe wird mit eine Kommentar mit dem Name des Package versehen.

```
<resources>
    <!-- gamesettings -->
    <string name="start_button">Start Game!</string>
    <string name="end_button">End Game!</string>
    ...

    <!-- mainmenu -->
    <string name="welcome_msg">Hello, World!</string>
    <string name="test_msg">This is a test message!</string>
    ...
</resources>

```

## Styles und Farben

Alle Styles eines GUI Elements werden in der "res/values/styles.xml" festgelegt.
Dabei werden für GUI Elemente separate Styles angelegt, die dann zum "AppTheme"
Style hinzugefügt werden.

Alle Farben werden in "res/values/colors.xml" definiert und nur über
"R.color.name" referenziert.
