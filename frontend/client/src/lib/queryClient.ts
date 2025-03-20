import { QueryClient } from '@tanstack/react-query';

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
type ApiOptions = {
  on401?: 'throw' | 'returnNull';
  headers?: Record<string, string>;
};

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5 minutes
      retry: false,
      refetchOnWindowFocus: false,
    },
  },
});

export const apiRequest = async (
  method: HttpMethod,
  path: string,
  body?: unknown,
  options: ApiOptions = {}
): Promise<Response> => {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  const response = await fetch(path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
    credentials: 'include',
  });

  if (response.status === 401 && options.on401 === 'throw') {
    throw new Error('Unauthorized');
  }

  if (!response.ok && response.status !== 401) {
    const errorText = await response.text();
    throw new Error(errorText || `HTTP error ${response.status}`);
  }

  return response;
};

export const getQueryFn = <T>(options: ApiOptions = {}) => {
  return async ({ queryKey }: { queryKey: (string | number)[] }): Promise<T | null> => {
    const [path, ...rest] = queryKey;
    const url = typeof path === 'string' ? path : '';
    
    if (!url) {
      throw new Error('Invalid query key');
    }

    const response = await apiRequest('GET', url, undefined, options);
    
    if (response.status === 401 && options.on401 === 'returnNull') {
      return null;
    }
    
    return await response.json();
  };
};