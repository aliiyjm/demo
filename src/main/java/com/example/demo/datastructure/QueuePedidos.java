package com.example.demo.datastructure;

import com.example.demo.model.Pedido;

public class QueuePedidos {
    private Node front, rear;

    //Cola vacia = pedido == front == rear
    //Coloca el nuevo nodo en el rear
    public void enqueue(Pedido pedido) {
        Node newNode = new Node(pedido);
        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
    }

    //Retira el front
    public Pedido dequeue() {
        if (front == null) return null;
        Pedido pedido = front.data;
        front = front.next;
        if (front == null) rear = null;
        return pedido;
    }

    //Ver si la cola esta vacia
    public boolean isEmpty() { return front == null; }
}
