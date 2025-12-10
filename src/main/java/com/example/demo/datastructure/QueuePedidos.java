package com.example.demo.datastructure;

import com.example.demo.model.Pedido;

public class QueuePedidos {
    private Node front, rear;

    public void enqueue(Pedido pedido) {
        Node newNode = new Node(pedido);
        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
    }

    public Pedido dequeue() {
        if (front == null) return null;
        Pedido pedido = front.data;
        front = front.next;
        if (front == null) rear = null;
        return pedido;
    }

    public boolean isEmpty() { return front == null; }
}
