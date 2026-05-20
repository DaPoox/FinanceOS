# Icons — FinanceOS

## Stack

Library : `com.composables:icons-lucide-android:1.1.0`
Browsable index : https://lucide.dev/icons/

---

## Règle principale — autonomie CC

Le design de référence est dans `design_handoff/` (JSX). Chaque fichier contient
des SVG inline ou des emojis pour les icônes. Quand tu implémentes un composant :

1. Ouvre le fichier JSX correspondant
2. Identifie l'icône utilisée (SVG path ou emoji)
3. Trouve l'équivalent Lucide le plus proche sur https://lucide.dev/icons/
    - Cherche par forme ou sémantique ("house", "shopping-cart", "trending-up"...)
    - Critères : stroke, round linecap, round linejoin — même géométrie que les SVG du design
4. Utilise `Lucide.NomDeLIcone` avec les deux imports (voir Règles d'usage)
5. Si aucun équivalent satisfaisant n'existe → utilise le SVG inline converti en `ImageVector`

Tu n'as pas besoin de demander validation pour le choix d'icône — fais-le toi-même.

### Vérifier qu'un nom d'icône existe dans v1.1.0

Les noms Lucide ont évolué entre versions. `Home` n'existe pas dans v1.1.0 — c'est `House`.
Si un nom ne compile pas, inspecte le JAR :

```bash
jar tf ~/.gradle/caches/modules-2/files-2.1/com.composables/icons-lucide-android/1.1.0/*/icons-lucide-release.aar \
  | grep -i "NomCherché"
# Les icônes sont listées sous com/composables/icons/lucide/<Name>Kt.class
```

---

## Règles d'usage

```kotlin
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.House  // import individuel par icône utilisée

Icon(
    imageVector = Lucide.House,
    contentDescription = null,      // null si décoratif, string si actionnable
    tint = MaterialTheme.colorScheme.onSurface,
    modifier = Modifier.size(22.dp)
)
```

Taille standard :
- Nav bar : `22dp`
- Card / composant : `18dp`
- Button inline : `14-16dp`
- Action principale : `24dp`

---

## Emojis dans le design JSX (data.jsx)

Les enveloppes utilisent des emojis comme icônes (`⌂`, `◐`, `✈`, `∞`...).
Pour l'app Android, remplace-les par des `Lucide.*` équivalents.
Choisis toi-même l'icône Lucide la plus proche de la sémantique de l'enveloppe.

---

## Ce qu'il ne faut PAS faire

```kotlin
// ❌ Material Icons
Icons.Default.Home

// ❌ Mauvais nom d'objet (c'est Lucide, pas LucideIcons)
LucideIcons.House

// ❌ Emoji dans le code final (sauf cas exceptionnel validé)
Text("🏠")

// ❌ Sans taille
Icon(Lucide.House, null)

// ❌ Couleur hardcodée
Icon(Lucide.House, null, tint = Color(0xFF8ba4be))
```

---

## Fallback SVG → ImageVector

Si Lucide n'a pas d'équivalent :
1. Copie le path SVG depuis le JSX
2. Android Studio → `File → New → Vector Asset → Local file`
3. Nomme en snake_case → `res/drawable/ic_*.xml`
4. Utilise via `painterResource(R.drawable.ic_*)`