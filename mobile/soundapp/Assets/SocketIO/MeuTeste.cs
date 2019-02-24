using System;
using UnityEngine;
using WebSocketSharp;

public class MeuTeste : MonoBehaviour
{
    public string url = "ws://localhost:8090/events/";
    private double _currentX;
    private double _currentY;
    private double _currentZ;

    public WebSocket socket
    {
        get { return ws; }
    }

    private WebSocket ws;

    void Awake()
    {
        ws = new WebSocket(url);
        ws.OnOpen += OnOpen;
        ws.OnMessage += OnMessage;
        ws.OnError += OnError;
        ws.OnClose += OnClose;
    }

    void Start()
    {
        ws.Connect();
    }

    private void OnDestroy()
    {
        ws.Close();
    }

    private void OnClose(object sender, CloseEventArgs e)
    {
        Debug.Log("closed");
    }

    private void OnError(object sender, ErrorEventArgs e)
    {
        Debug.Log("error");
    }

    private void OnMessage(object sender, MessageEventArgs e)
    {
        //Debug.Log("message:" + e.Data);

        string[] parts = e.Data.Split(',');
        if (parts[0].Length > 0)
        {
            if ("POS".Equals(parts[0]))
            {
                string id = parts[1];
                _currentX = Double.Parse(parts[2]) * -100.0;
                //_currentY = Double.Parse(parts[3]);
                _currentZ = Double.Parse(parts[3]) * 100.0;
            }
        }
    }

    private void Update()
    {
        gameObject.transform.position = new Vector3((float) _currentX, (float) _currentY, (float) _currentZ);
    }

    private void OnOpen(object sender, EventArgs e)
    {
        Debug.Log("opened");
    }
}