# Configuration Authentification GitHub avec Frontend

## 1. Configuration GitHub OAuth App

### Créer une OAuth App sur GitHub :
1. Aller sur https://github.com/settings/developers
2. Cliquer "New OAuth App"
3. Remplir :
   - **Application name** : `Inventory Management`
   - **Homepage URL** : `http://localhost:3000` (ou votre URL frontend)
   - **Authorization callback URL** : `http://localhost:8282/login/oauth2/code/github`

### Récupérer les credentials :
- **Client ID** : `your_github_client_id`
- **Client Secret** : `your_github_client_secret`

## 2. Configuration Backend (.env)

```env
# GitHub OAuth
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

# Frontend URLs (pour redirection après login)
FRONTEND_URL=http://localhost:3000,http://localhost:4200
```

## 3. Configuration Frontend

### React/Next.js :
```javascript
// Bouton de connexion GitHub
const handleGitHubLogin = () => {
  window.location.href = 'http://localhost:8282/oauth2/authorization/github';
};

// Composant Login
<button onClick={handleGitHubLogin} className="github-login-btn">
  <i className="fab fa-github"></i>
  Se connecter avec GitHub
</button>
```

### Angular :
```typescript
// service/auth.service.ts
loginWithGitHub() {
  window.location.href = 'http://localhost:8282/oauth2/authorization/github';
}

// component
<button (click)="authService.loginWithGitHub()" class="github-login-btn">
  <i class="fab fa-github"></i>
  Se connecter avec GitHub
</button>
```

### Vue.js :
```javascript
// methods
loginWithGitHub() {
  window.location.href = 'http://localhost:8282/oauth2/authorization/github';
}

// template
<button @click="loginWithGitHub" class="github-login-btn">
  <i class="fab fa-github"></i>
  Se connecter avec GitHub
</button>
```

## 4. Gestion de la Redirection

### Page de callback frontend :
```javascript
// pages/auth/callback.js ou /auth/callback
useEffect(() => {
  const urlParams = new URLSearchParams(window.location.search);
  const token = urlParams.get('token');
  const error = urlParams.get('error');
  
  if (token) {
    // Stocker le token
    localStorage.setItem('authToken', token);
    // Rediriger vers dashboard
    router.push('/dashboard');
  } else if (error) {
    // Gérer l'erreur
    console.error('Erreur authentification:', error);
    router.push('/login?error=' + error);
  }
}, []);
```

## 5. CSS pour le bouton GitHub

```css
.github-login-btn {
  background-color: #333;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  transition: background-color 0.3s;
}

.github-login-btn:hover {
  background-color: #24292e;
}

.github-login-btn i {
  font-size: 18px;
}
```

## 6. Test de l'intégration

### URLs de test :
- **Login GitHub** : http://localhost:8282/oauth2/authorization/github
- **API Profile** : http://localhost:8282/api/auth/profile (avec token)
- **Frontend** : http://localhost:3000/auth/callback

### Flux complet :
1. User clique sur "Se connecter avec GitHub"
2. Redirection vers GitHub OAuth
3. User autorise l'application
4. GitHub redirige vers backend callback
5. Backend génère JWT et redirige vers frontend
6. Frontend récupère le token et connecte l'user

## 7. Gestion des erreurs

```javascript
// Intercepteur pour gérer les erreurs d'auth
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

## 8. Configuration CORS (déjà fait)

Le backend est configuré pour accepter les requêtes du frontend :
- http://localhost:3000 (React/Next.js)
- http://localhost:4200 (Angular)

## 9. ENDPOINTS PAR RÔLE

### 🔓 PUBLIC ACCESS (Aucune authentification requise)
- `POST /api/v1/auth/**` - Authentification (login, register, forgot-password)
- `GET /swagger-ui/**` - Documentation Swagger
- `GET /api/demo/**` - Endpoints de démonstration
- `GET /oauth2/**` - OAuth2 (GitHub, Google)

### 👤 ROLE_USER (Consultation uniquement)
**Articles :**
- `GET /api/v1/articles/all` - Liste tous les articles
- `GET /api/v1/articles/{id}` - Détails d'un article
- `GET /api/v1/articles/code/{code}` - Article par code
- `GET /api/v1/articles/archived` - Articles archivés

**Catégories :**
- `GET /api/v1/categories/all` - Liste toutes les catégories
- `GET /api/v1/categories/{id}` - Détails d'une catégorie
- `GET /api/v1/categories/by-company/{companyId}` - Catégories par entreprise

**Entreprises :**
- `GET /api/v1/companies/all` - Liste toutes les entreprises
- `GET /api/v1/companies/{id}` - Détails d'une entreprise

**Clients :**
- `GET /api/v1/clients/**` - Toutes les opérations clients

**Commandes :**
- `GET /api/v1/orders/all` - Liste toutes les commandes
- `GET /api/v1/orders/client/{clientId}` - Commandes par client
- `GET /api/v1/orders/status/{status}` - Commandes par statut
- `GET /api/v1/orders/**` - Autres opérations commandes

### 💼 ROLE_SALES (ROLE_USER + Gestion ventes)
**Tout ce que ROLE_USER peut faire +**

**Fournisseurs :**
- `GET /api/v1/suppliers/**` - Consultation fournisseurs

**Commandes fournisseurs :**
- `GET /api/v1/supplier-orders/{id}` - Détails commande fournisseur
- `GET /api/v1/supplier-orders/code/{code}` - Commande par code
- `GET /api/v1/supplier-orders/all` - Liste commandes fournisseurs

**Lignes commandes fournisseurs :**
- `GET /api/v1/supplier-order-lines/{id}` - Détails ligne commande
- `GET /api/v1/supplier-order-lines/order/{supplierOrderId}` - Lignes par commande
- `GET /api/v1/supplier-order-lines/all` - Toutes les lignes

**Ventes :**
- `GET /api/v1/sales/{id}` - Détails d'une vente
- `GET /api/v1/sales` - Liste des ventes

**Lignes commandes :**
- `ALL /api/v1/order-lines/**` - Gestion lignes de commandes

### 🏢 ROLE_MANAGER (ROLE_SALES + Gestion opérationnelle)
**Tout ce que ROLE_SALES peut faire +**

**Articles :**
- `POST /api/v1/articles/create` - Créer article
- `PUT /api/v1/articles/update/{id}` - Modifier article
- `POST /api/v1/articles/{id}/image` - Upload image article

**Catégories :**
- `POST /api/v1/categories/create` - Créer catégorie
- `PUT /api/v1/categories/update` - Modifier catégorie

**Entreprises :**
- `POST /api/v1/companies/create` - Créer entreprise

**Commandes :**
- `PUT /api/v1/orders/{id}/cancel` - Annuler commande

**Ventes :**
- `POST /api/v1/sales/create` - Créer vente
- `PUT /api/v1/sales/{id}/status` - Changer statut vente
- `PUT /api/v1/sales/{id}/cancel` - Annuler vente
- `POST /api/v1/sales/{id}/generate-lines` - Générer lignes vente

**Commandes fournisseurs :**
- `POST /api/v1/supplier-orders/create` - Créer commande fournisseur
- `PUT /api/v1/supplier-orders/{id}/status` - Changer statut
- `PUT /api/v1/supplier-orders/{id}/cancel` - Annuler commande

**Lignes commandes fournisseurs :**
- `POST /api/v1/supplier-order-lines/add` - Ajouter ligne
- `PUT /api/v1/supplier-order-lines/{id}` - Modifier ligne

### 👑 ROLE_ADMIN (Accès complet)
**Tout ce que ROLE_MANAGER peut faire +**

**Utilisateurs :**
- `ALL /api/v1/users/**` - Gestion complète utilisateurs (sauf update-password)

**Articles :**
- `DELETE /api/v1/articles/**` - Suppression articles

**Entreprises :**
- `ALL /api/v1/companies/**` - Gestion complète entreprises

**Ventes :**
- `PUT /api/v1/sales/{id}/finalize` - Finaliser vente
- `ALL /api/v1/sales/**` - Gestion complète ventes

**Commandes fournisseurs :**
- `ALL /api/v1/supplier-orders/**` - Gestion complète

**Lignes commandes fournisseurs :**
- `DELETE /api/v1/supplier-order-lines/**` - Suppression lignes

### 🔐 AUTHENTICATED (Tous les utilisateurs connectés)
- `PUT /api/v1/users/update-password` - Changer son mot de passe

## Notes importantes :
- ✅ Backend OAuth2 déjà configuré
- ✅ Auto-registration activée (ROLE_USER par défaut)
- ✅ JWT token généré automatiquement
- ✅ Redirection frontend configurée
- 🔧 Il faut juste créer l'OAuth App sur GitHub et configurer le frontend