import asyncio
import websockets
from ahk import AHK
import time

ahk = AHK()

async def handler(ws):
    async for msg in ws:
        print("Received:", msg)

        # Optional: focus Roblox window
        for win in ahk.windows():
            if "roblox" in win.title.lower():
                win.activate()
                break

        # time.sleep(0.5)

        # Open chat (Alt + /), wait, type message, press Enter
        # ahk.send("!/")
        # ahk.send("/")
        ahk.zsend("{Enter}")
        # ahk.send("/")
        time.sleep(1)
        ahk.send(msg)
        # ahk.send_input(msg)
        ahk.send("{Enter}")

async def main():
    async with websockets.serve(handler, "0.0.0.0", 8765):
        print("WebSocket server listening on ws://0.0.0.0:8765")
        await asyncio.Future()  # Run forever

asyncio.run(main())
