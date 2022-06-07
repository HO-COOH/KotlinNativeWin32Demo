@file:Suppress("FunctionName")

import kotlinx.cinterop.*
import platform.windows.*;

const val WindowClass = "My Window";

fun WindowProc(hwnd: HWND?, uMsg: UINT, wParam: WPARAM, lParam: LPARAM): LRESULT
{
    return DefWindowProcW(hwnd, uMsg, wParam, lParam);
}

fun RegisterWindowClass()
{
    memScoped()
    {
        val wc = alloc<WNDCLASS>();

        wc.lpszClassName = WindowClass.wcstr.ptr
        wc.lpfnWndProc = staticCFunction(::WindowProc)
        wc.hInstance = GetModuleHandleW(null)

        RegisterClass?.let { it(wc.ptr) };
    }
}

fun main()
{
    RegisterWindowClass();

    val hwnd = CreateWindowExW(
        0, WindowClass, "My Window", WS_OVERLAPPEDWINDOW,
        CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, null, null, GetModuleHandleW(null), null
    );

    ShowWindow(hwnd, SW_SHOW);

    memScoped()
    {
        val msg = alloc<MSG>();
        while(GetMessage?.let { it(msg.ptr, null, 0u, 0u) }!! > 0)
        {
            TranslateMessage(msg.ptr);
            DispatchMessage?.let { it(msg.ptr) };
        }
    }
}