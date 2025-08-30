import { useState } from 'react';

export function useAsyncState(initialData = []) {
  const [data, setData] = useState(initialData);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const load = async (asyncFn) => {
    setLoading(true);
    setError('');
    try {
      const result = await asyncFn();
      setData(result);
      setError('');
      return result;
    } catch (err) {
      setError(err.response?.data?.errorMessage || err.message || 'Ошибка');
      setData(initialData);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { data, setData, error, setError, loading, setLoading, load };
}
