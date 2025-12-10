package com.example.demo.datastructure;

import com.example.demo.model.Pedido;

public class Node {
    public Pedido data;
    public Node next;

    public Node(Pedido data) {
        this.data = data;
        this.next = null;
    }
}
