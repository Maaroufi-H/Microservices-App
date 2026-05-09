# Agent Communication Log

## État global

- Phase courante : `SETUP`
- Agent actif : _(aucun — en attente de la première mission)_
- Dernière mise à jour : _(à remplir par chaque agent)_

---

## ⏳ Décisions en attente d'approbation utilisateur

<!-- 
  Le CHEF D'ÉQUIPE écrit ici les décisions FONCTIONNELLES avant d'agir.
  Format :
  ### Décision #N — [titre court]
  - Description : ...
  - Impact : ...
  - Options : A) ... B) ...
  - En attente de : utilisateur
  
  Vide = aucune décision en attente, l'Orchestrateur peut continuer.
-->

_(aucune décision en attente)_

---

## Journal des actions

<!-- 
  Chaque agent écrit ici son entrée dans le format suivant :
  ### [NOM-AGENT] — [Action]
  - Ce qu'il a fait
  - Fichiers modifiés
  - Status : ✅ / ❌ / ⚠️
  - Prochain agent à appeler : NOM-AGENT
-->

_(log vide — première exécution)_

---

## Instructions en attente pour les agents

<!--
  L'ORCHESTRATEUR écrit ici les instructions pour l'agent suivant AVANT de l'appeler.
  L'agent lit cette section, exécute, puis l'efface et écrit son rapport dans le Journal.
  
  Format :
  ### Pour DATABASE-AGENT
  - Tâche : switcher les 7 services de PostgreSQL vers H2
  - Priorité : HAUTE
  - Contrainte : ne pas modifier les services non listés
-->

_(aucune instruction en attente)_

---

## Référence rapide — Ordre d'exécution recommandé

```
1. VERSIONING-AGENT   → checkout + créer branche feature/local-ecommerce-test
2. DATABASE-AGENT     → switcher tous les services vers H2
3. BACKEND-AGENT      → implémenter POST /api/orders + CORS
4. FRONTEND-AGENT     → créer SPA e-commerce dans gateway-service/static/
5. DEVOPS-AGENT       → scripts build-all.sh, start-local.sh, stop-local.sh
6. TESTING-AGENT      → test E2E complet + rapport final
7. VERSIONING-AGENT   → commits atomiques + tag local-test-v1.0
```
