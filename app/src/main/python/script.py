import numpy as np
from scipy.stats import multivariate_normal

def gmm_alg(M):
    def mode(ls):
        counts = {}
        for item in ls:
            if item in counts:
                counts[item] += 1
            else:
                counts[item] = 1
        return [key for key in counts.keys() if counts[key] == max(counts.values())]
    def correction(arr):
        max_mode, min_mode = max(mode(arr)), min(mode(arr))
        k = 0
        for i in range(len(arr)):
            if(i == len(arr) - k ):
                break
        if arr[i - k] > 1.2 * max_mode:
            arr[i - k] = max_mode
            arr.insert(i, max_mode)
            k -= 1
        if arr[i - k] < 0.8 * min_mode:
            arr.pop(i - k)
            i -= 1
            k += 1
        return arr
    M=correction(M)
    x = np.zeros((len(M), 2))
    for i, a in enumerate(M):
        x[i] = [i, a**2]
    gm = GMM(2)
    gm.fit(x)
    clusters = gm.predict(x)
    first = []
    zeros = []
    for i, j in enumerate(clusters):
        if j == 1:
            first.insert(0, M[i])
        else:
            zeros.insert(0, M[i])
    watermark = ""
    mode_zeros = mode(zeros)
    mode_first = mode(first)
    if mode(mode_zeros) > mode(mode_first):
        for j in clusters:
            if j == 0:
                watermark += "1"
            else:
                watermark += "0"
    else:
        for j in clusters:
            if j == 0:
                watermark += "0"
            else:
                watermark += "1"
    return watermark




class GMM:
    def __init__(self, k, max_iter=5):
        self.k = k
        self.max_iter = int(max_iter)

    def initialize(self, X):
        self.shape = X.shape
        self.n, self.m = self.shape

        self.phi = np.full(shape=self.k, fill_value=1/self.k)
        self.weights = np.full(shape=self.shape, fill_value=1/self.k)
        # random_row = [1,3,3,3,3]
        random_row = np.random.randint(low=0, high=self.n, size=self.k)
        self.mu = [  X[row_index,:] for row_index in random_row ]
        self.sigma = [ np.cov(X.T) for _ in range(self.k) ]

    def e_step(self, X):
        self.weights = self.predict_proba(X)
        self.phi = self.weights.mean(axis=0)

    def m_step(self, X):
        for i in range(self.k):
            weight = self.weights[:, [i]]
            total_weight = weight.sum()
            self.mu[i] = (X * weight).sum(axis=0) / total_weight
            self.sigma[i] = np.cov(X.T,
                                   aweights=(weight/total_weight).flatten(),
                                   bias=True)

    def fit(self, X):
        self.initialize(X)
        for iteration in range(self.max_iter):
            self.e_step(X)
            self.m_step(X)

    def predict_proba(self, X):
        likelihood = np.zeros( (self.n, self.k) )
        for i in range(self.k):
            distribution = multivariate_normal(
                mean=self.mu[i],
                cov=self.sigma[i])
            likelihood[:,i] = distribution.pdf(X)
        numerator = likelihood * self.phi
        denominator = numerator.sum(axis=1)[:, np.newaxis]
        weights = numerator / denominator
        return weights

    def predict(self, X):
        weights = self.predict_proba(X)
        return np.argmax(weights, axis=1)
