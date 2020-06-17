# SWE2 - PicDB - Semesterprojekt

## Aufgabenstellung
Ziel des Semesterprojekts ist die Erstellung eines Programms mit Grafikoberfläche, in dem Bilder angezeigt und deren Informationen bearbeitet werden können.
Die grafische Oberfläche ist mittels JavaFx umzusetzen, die Komponenten sind in Layer zu kapseln, die Grafik-Klassen als Model-ViewModel-Controller zu verwirklichen.

Das fertige Programm soll folgende Funktionen enthalten:
- Verwalten von Bilder aus einem Verzeichnis
- Auslesen & Ändern der IPTC & EXIF Informationen des Bildes (Simulieren!)
- Speichern der IPTC & EXIF Informationen in einer Datenbank
- Verwaltung von Fotografen_innen in einer Datenbank
- Zuordnung der Fotografen_innen zu Bildern
- Suche nach Bilder anhand der IPTC, EXIF und Fotografen_innen Daten
---

## Benutzerhandbuch

### Menüband
Nach dem Start gelangt der User zur Hauptansicht, auf der er verschiedene Optionen oben im Menüband findet.
#### Bild hinzufügen
Mittels Add->AddPicture kann ein Foto in die Datenbank geladen und dessen IPTC Daten und den Fotografen des Bildes eingetragen werden.
#### Fotografen anzeigen
In View->View Photographers kann der User sich alle in der Datenbank gelisteten Fotografen anzeigen lassen. Nur diese sind auch als Bildeigentümer zulässig. In dem Menü kann der User auch weiter Fotografendaten bearbeiten oder löschen.
#### Reporting
Im Reiter Reporting befinden sich zwei Auswahlen.<br>
Generate Picture Report erstellt ein PDF mit allen verfügbaren Informationen des gerade ausgewählten Fotos. ACHTUNG: es muss dazu ein Bild in der Bilderleiste angeklickt sein, sonst ist diese Funktion deaktiviert.<br>
Generate Tag Report erstellt ein PDF inklusive Grafik über die aktuell genutzten Tags der Bilder und die Häufigkeit der Verwendung.


### Suchleiste
In die Suchleiste können Tags oder Namen gesucht werden. Nach Eingabe zeigt die Bildleiste nur noch Fotos an, welche in Zusammenhang mit dem geschriebenen Suchbegriff stehen.
Befindet sich der User in einer aktiven Suchanzeige kann er über den neuen Reset-Button zurück zur Gesamtansicht wechseln.
### Bildanzeige und Informationstabs
Links wird das aktuell angeklickte Bild in groß angezeigt. Das Fenster ist dynamisch und das Foto lässt sich somit beliebig skalieren. 
#### IPTC
Die bearbeitbaren IPTC-Daten und der Name des Fotografen werden in einem Infotab angezeigt, falls diese bereits eingegeben wurden. Möchte der User einen Eintrag ändern, kann er das jeweilige Textfeld anklicken und dort Änderungen vornehmen. Mit Klick auf den Save-Button werden die Eingaben dann gespeichert und in die Datenbank eingetragen.
#### EXIF
Beim Klick auf den EXIF-Infotab kann der User mit einem Klick auf die Auswahlleiste die aus dem Bild extrahierten EXIF-Informationen ansehen. Diese sind nicht veränderbar.


### Bildleiste
Hier kann der User in der Fotodatenbank befindliche Fotos sehen und auf Klick die Informationen erhalten.  



## Lösungsbeschreibung
Die Aufgabe ist eine Einzelarbeit von Maximilian Rotter (if18b136). <br>
Es wurde zuerst die Datenbank (Einsatz von SoftwareParadigmen) und die GUI entworfen (JavaFX SceneBuilder).
Danach wurden schrittweise durch Refactoring und unter Einhaltung von Codemetriken die Modelle, ViewModelle und Controller erstellt.<br>
Nach fertigstellung der Grundfunktionen wurde das Programm um weitere Funktionen (Reports, intelligente Tags, usw...) erweitert.


## Worauf bin ich stolz

Darauf, dass ich das Projekt als Einzelperson gelöst habe.  <br>
Weiters habe ich die Datenbank über die Grundanforderungen heraus erweitert, um IPTC und EXIF Daten dynamisch zu machen, sowie Tags extra zu archivieren.<br>


## Was würde ich nächstes mal anders machen

Von anfang an das Projekt mit einem Build-System initialisieren. Mitten im Projekt war das leider nicht mehr organisch realisierbar. <br>
Mit den Modelelementen anfangen, da ich bei der Implementierung schnell mit dem JavaFX Binding verzweifelt bin, da ich es im Nachhinein einfügen wollte, was leider bis zuletzt nur in einer Instanz der Suchleiste funktioniert hat.