package com.example.demo.datastructure;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.Pedido;

public class SinglyLinkedList {
    private Node head;

    //Lista vacia == newNode == head
    //Recorrer la lista desde head hasta el final
    public void addLast(Pedido pedido) {
        Node newNode = new Node(pedido);
        if (head == null) head = newNode;
        else {
            Node current = head;
            while (current.next != null) current = current.next;
            current.next = newNode;
        }
    }

    //Recorre la lista nodo por nodo hasta encontrar el id
    public Pedido findById(int id) {
        Node current = head;
        while (current != null) {
            if (current.data.getId() == id) return current.data;
            current = current.next;
        }
        return null;
    }

    //Recorre la lista nodo por nodo hasta encontrar el id
    public boolean removeById(int id) {
        if (head == null) return false;
        if (head.data.getId() == id) {
            head = head.next;
            return true;
        }
        Node current = head;
        while (current.next != null) {
            if (current.next.data.getId() == id) {
                current.next = current.next.next;
                return true;
            }
            current = current.next;
        }
        return false;
    }
    

    public List<Pedido> toList() {
    List<Pedido> list = new ArrayList<>();
    Node current = head;
    while (current != null) {
        list.add(current.data);
        current = current.next;
        }
        return list;
    }

    //Incrementa un contador por nodo y devuelve el total de pedidos
    public int size() {
        int count = 0;
        Node current = head;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    public Node getHead() { return head; }
}
