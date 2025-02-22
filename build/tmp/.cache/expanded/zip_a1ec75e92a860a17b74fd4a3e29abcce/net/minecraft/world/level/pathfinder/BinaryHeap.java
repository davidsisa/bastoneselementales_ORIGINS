package net.minecraft.world.level.pathfinder;

import java.util.Arrays;

public class BinaryHeap {
    private Node[] heap = new Node[128];
    private int size;

    public Node insert(Node pPoint) {
        if (pPoint.heapIdx >= 0) {
            throw new IllegalStateException("OW KNOWS!");
        } else {
            if (this.size == this.heap.length) {
                Node[] anode = new Node[this.size << 1];
                System.arraycopy(this.heap, 0, anode, 0, this.size);
                this.heap = anode;
            }

            this.heap[this.size] = pPoint;
            pPoint.heapIdx = this.size;
            this.upHeap(this.size++);
            return pPoint;
        }
    }

    public void clear() {
        this.size = 0;
    }

    public Node peek() {
        return this.heap[0];
    }

    public Node pop() {
        Node node = this.heap[0];
        this.heap[0] = this.heap[--this.size];
        this.heap[this.size] = null;
        if (this.size > 0) {
            this.downHeap(0);
        }

        node.heapIdx = -1;
        return node;
    }

    public void remove(Node pNode) {
        this.heap[pNode.heapIdx] = this.heap[--this.size];
        this.heap[this.size] = null;
        if (this.size > pNode.heapIdx) {
            if (this.heap[pNode.heapIdx].f < pNode.f) {
                this.upHeap(pNode.heapIdx);
            } else {
                this.downHeap(pNode.heapIdx);
            }
        }

        pNode.heapIdx = -1;
    }

    public void changeCost(Node pPoint, float pCost) {
        float f = pPoint.f;
        pPoint.f = pCost;
        if (pCost < f) {
            this.upHeap(pPoint.heapIdx);
        } else {
            this.downHeap(pPoint.heapIdx);
        }
    }

    public int size() {
        return this.size;
    }

    private void upHeap(int pIndex) {
        Node node = this.heap[pIndex];
        float f = node.f;

        while (pIndex > 0) {
            int i = pIndex - 1 >> 1;
            Node node1 = this.heap[i];
            if (!(f < node1.f)) {
                break;
            }

            this.heap[pIndex] = node1;
            node1.heapIdx = pIndex;
            pIndex = i;
        }

        this.heap[pIndex] = node;
        node.heapIdx = pIndex;
    }

    private void downHeap(int pIndex) {
        Node node = this.heap[pIndex];
        float f = node.f;

        while (true) {
            int i = 1 + (pIndex << 1);
            int j = i + 1;
            if (i >= this.size) {
                break;
            }

            Node node1 = this.heap[i];
            float f1 = node1.f;
            Node node2;
            float f2;
            if (j >= this.size) {
                node2 = null;
                f2 = Float.POSITIVE_INFINITY;
            } else {
                node2 = this.heap[j];
                f2 = node2.f;
            }

            if (f1 < f2) {
                if (!(f1 < f)) {
                    break;
                }

                this.heap[pIndex] = node1;
                node1.heapIdx = pIndex;
                pIndex = i;
            } else {
                if (!(f2 < f)) {
                    break;
                }

                this.heap[pIndex] = node2;
                node2.heapIdx = pIndex;
                pIndex = j;
            }
        }

        this.heap[pIndex] = node;
        node.heapIdx = pIndex;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public Node[] getHeap() {
        return Arrays.copyOf(this.heap, this.size);
    }
}