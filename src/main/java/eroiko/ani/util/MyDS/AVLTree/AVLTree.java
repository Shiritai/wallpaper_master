package eroiko.ani.util.MyDS.AVLTree;
import java.util.ArrayList;
import java.util.Comparator;

public class AVLTree<K extends Comparable<K>, V>{
    private class Node {
        public K key;
        public V value;
        public Node left, right;
        public int height; // 以高度作為平衡因子
        
        public Node(K key, V value){
            this.key = key;
            this.value = value;
            this.left = this.right = null;
            this.height = 1; // 葉子節點的 height
        }
    }

    private Node root;
    private int size;
    private final Comparator<? super K> comparator;
    
    public AVLTree(){
        root = null;
        size = 0;
        comparator = null;
    }

    public AVLTree(Comparator<? super K> comparator){
        root = null;
        size = 0;
        this.comparator = comparator;
    }

    /** return the height of node, 為了處理 cur == null 的情況而封裝成函數 */
    private int getHeight(Node cur){
        if (cur == null){
            return 0;
        }
        return cur.height;
    }

    public boolean isEmpty(){ return size == 0;}

    public int getSize(){ return size;}

    public void add(K key, V value){
        root = add(root, key, value);
    }

    private int getBalanceFactor(Node cur){
        if (cur == null){
            return 0;
        }
        return getHeight(cur.left) - getHeight(cur.right);
    }

    /** 確認並調整不平衡的樹, 返回其根 */
    private Node adjust(Node cur){
        if (cur != null){
            /* 更新 height */
            cur.height = 1 + Math.max(getHeight(cur.left), getHeight(cur.right));
            /* 計算平衡因子 */
            int balanceFactor = getBalanceFactor(cur);
            if (balanceFactor > 1 || balanceFactor < -1){
                /* LL (向左傾斜) -> 右旋轉 */
                if (balanceFactor > 1 && getBalanceFactor(cur.left) >= 0){
                    return rightRotate(cur);
                }
                /* RR (向右傾斜) -> 左旋轉 */
                if (balanceFactor < -1 && getBalanceFactor(cur.right) <= 0){
                    return leftRotate(cur);
                }
                /* * 
                * LR -> 左旋轉 -> 右旋轉
                * 記憶方法 : LR 正好是 L(左) R(右)
                * */
                if (balanceFactor > 1 && getBalanceFactor(cur.left) < 0){ // 左子樹的左子樹矮於左子樹的右子樹
                    /* 對左子樹左旋轉 */
                    cur.left = leftRotate(cur.left);
                    /* 右旋轉 */
                    return rightRotate(cur);
                }
                /* * 
                    * RL -> 左旋轉 -> 右旋轉
                    * 記憶方法 : RL 正好是 R(右) L(左)
                    * */
                if (balanceFactor < -1 && getBalanceFactor(cur.right) > 0){ // 右子樹的左子樹高於右子樹的右子樹
                    /* 對右子樹右旋轉 */
                    cur.right = rightRotate(cur.right);
                    /* 右旋轉 */
                    return leftRotate(cur);
                }
            }
        }
        return cur;
    }
    
    /* Recur, 以 cur 為 root node, 插入 (key, value) */
    private Node add(Node cur, K key, V value){
        if (cur == null){
            ++size;
            return new Node(key, value);
        }
        if (comparator == null){
            if (key.compareTo(cur.key) < 0)
                cur.left = add(cur.left, key, value);
            else if (key.compareTo(cur.key) > 0)
                cur.right = add(cur.right, key, value);
            else // key.compareTo(cur.key) == 0
                cur.value = value;
        }
        else {
            if (comparator.compare(key, cur.key) < 0)
                cur.left = add(cur.left, key, value);
            else if (comparator.compare(key, cur.key) > 0)
                cur.right = add(cur.right, key, value);
            else // key.compareTo(cur.key) == 0
                cur.value = value;
        }
        
        return adjust(cur);
    }

    /** 右旋轉 */
    private Node rightRotate(Node unBalancedRoot){
        var newRoot = unBalancedRoot.left;
        unBalancedRoot.left = newRoot.right;
        newRoot.right = unBalancedRoot;
        /* 調整高度, 自 unBalancedRoot (低者) 到 newRoot (高者) */
        unBalancedRoot.height = Math.max(getHeight(unBalancedRoot.left), getHeight(unBalancedRoot.right)) + 1;
        newRoot.height = Math.max(getHeight(newRoot.left), getHeight(newRoot.right)) + 1;
        return newRoot;
    }

    /** 左旋轉 */
    private Node leftRotate(Node unBalancedRoot){
        var newRoot = unBalancedRoot.right;
        unBalancedRoot.right = newRoot.left;
        newRoot.left = unBalancedRoot;
        /* 調整高度, 自 unBalancedRoot (低者) 到 newRoot (高者) */
        unBalancedRoot.height = Math.max(getHeight(unBalancedRoot.left), getHeight(unBalancedRoot.right)) + 1;
        newRoot.height = Math.max(getHeight(newRoot.left), getHeight(newRoot.right)) + 1;
        return newRoot;
    }

    private Node getNode(Node tmp, K key){
        if (tmp != null){
            if (comparator == null){
                if (key.compareTo(tmp.key) == 0)
                    return tmp;
                else if (key.compareTo(tmp.key) < 0)
                    return getNode(tmp.left, key);
                else // key.compareTo(cur.key) > 0
                    return getNode(tmp.right, key);
            }
            else {
                if (comparator.compare(key, tmp.key) == 0)
                    return tmp;
                else if (comparator.compare(key, tmp.key) < 0)
                    return getNode(tmp.left, key);
                else // comparator.compare(key, cur.key) > 0
                    return getNode(tmp.right, key);
                
            }
        }
        return null;
    }

    public boolean exist(K key){ return getNode(root, key) != null;}

    public V get(K key){
        Node tmp = getNode(root, key);
        return tmp == null ? null : tmp.value;
    }
    
    public void change(K key, V value){
        Node tmp = getNode(root, key);
        if (tmp != null)
            tmp.value = value;
        else
            throw new IllegalArgumentException(key + "does not exist!");
    }

    public V remove(K key){
        Node tmp = getNode(root, key);
        if (tmp != null){
            root = remove(root, key);
            return tmp.value;
        }
        return null;
    }

    private Node remove(Node cur, K key){
        Node retNode = null;
        if (cur != null){
            if (comparator == null){
                if (key.compareTo(cur.key) == 0){
                    --size;
                    if (cur.left == null){
                        Node comb = cur.right;
                        if (comb != null){
                            comb.right = null; // comb.left is already == null
                        }
                        retNode = comb;
                    }
                    else if (cur.right == null){
                        Node comb = cur.left;
                        if (comb != null){
                            comb.left = null; // comb.right is already == null
                        }
                        retNode = comb;
                    }
                    else { // both of cur's child exist!
                        Node successor = cur.right;
                        Node parentSuc = null;
                        while (successor.left != null){
                            parentSuc = successor;
                            successor = successor.left;
                        }
                        
                        successor.left = cur.left;
                        
                        if (parentSuc != null){
                            parentSuc.left = successor.right; // 覆蓋原本後繼節點 -> 維護 AVL
                            parentSuc = adjust(parentSuc);
                            
                            successor.right = cur.right;
                        }
    
                        cur.left = cur.right = null;
                        retNode = successor;
                    }
                }
                else if (key.compareTo(cur.key) < 0)
                    cur.left = remove(cur.left, key);
                else // key.compareTo(cur.key) < 0
                    cur.right = remove(cur.right, key);
            }
            else {
                if (comparator.compare(key, cur.key) == 0){
                    --size;
                    if (cur.left == null){
                        Node comb = cur.right;
                        if (comb != null){
                            comb.right = null; // comb.left is already == null
                        }
                        retNode = comb;
                    }
                    else if (cur.right == null){
                        Node comb = cur.left;
                        if (comb != null){
                            comb.left = null; // comb.right is already == null
                        }
                        retNode = comb;
                    }
                    else { // both of cur's child exist!
                        Node successor = cur.right;
                        Node parentSuc = null;
                        while (successor.left != null){
                            parentSuc = successor;
                            successor = successor.left;
                        }
                        
                        successor.left = cur.left;
                        
                        if (parentSuc != null){
                            parentSuc.left = successor.right; // 覆蓋原本後繼節點 -> 維護 AVL
                            parentSuc = adjust(parentSuc);
                            
                            successor.right = cur.right;
                        }
    
                        cur.left = cur.right = null;
                        retNode = successor;
                    }
                }
                else if (comparator.compare(key, cur.key) < 0)
                    cur.left = remove(cur.left, key);
                else // comparator.compare(key, cur.key) < 0
                    cur.right = remove(cur.right, key);

            }
        }
        return adjust(retNode);
    }

    /** 輔助判斷函數, 利用 inorder 特性判斷 */
    public boolean isBST(){
        var tmpList = new ArrayList<K>();
        inOrder(root, tmpList);
        for (int i = 0; i < tmpList.size() - 1; ++i){
            if (tmpList.get(i).compareTo(tmpList.get(i + 1)) > 0){
                return false;
            }
        }
        return true;
    }

    public ArrayList<V> values(){
        var res = new ArrayList<V>(size);
        getValueInOrder(root, res);
        return res;
    }
    
    private void getValueInOrder(Node cur, ArrayList<V> list){
        if (cur != null){
            getValueInOrder(cur.left, list);
            list.add(cur.value);
            getValueInOrder(cur.right, list);
        }
    }

    private void inOrder(Node cur, ArrayList<K> list){
        if (cur != null){
            inOrder(cur.left, list);
            list.add(cur.key);
            inOrder(cur.right, list);
        }
    }

    public boolean isBalanced(){
        return isBalanced(root);
    }

    public boolean isBalanced(Node cur){
        if (cur == null){
            return true;
        }
        int balanceFactor = getBalanceFactor(cur);
        if (Math.abs(balanceFactor) > 1){
            return false;
        }
        return isBalanced(cur.left) && isBalanced(cur.right);
    }
}
